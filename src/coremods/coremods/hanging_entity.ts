import { AbstractInsnNode, ASMAPI, CoreMods, InsnList, MethodInsnNode, MethodNode, Opcodes, VarInsnNode } from "coremods";

function initializeCoreMod(): CoreMods {
    return {
        hanging_entity: {
            target: {
                type: 'METHOD',
                class: 'net.minecraft.world.item.HangingEntityItem',
                methodName: 'm_6225_',
                methodDesc: '(Lnet/minecraft/world/item/context/UseOnContext;)Lnet/minecraft/world/InteractionResult;'
            },
            transformer: (method: MethodNode) => {
                let lvtEntity = -2;
                let insertBefore: AbstractInsnNode | null = null

                for (let i = 0; i < method.instructions.size(); i++) {
                    const insn = method.instructions.get(i)
                    if (insn == null) continue

                    if (lvtEntity == -2 && insn.getOpcode() == Opcodes.INVOKESPECIAL) {
                        const methodInsn = insn as MethodInsnNode
                        if (methodInsn.owner == 'net/minecraft/world/entity/decoration/ItemFrame' && methodInsn.name == '<init>') {
                            lvtEntity = -1
                        }
                    }

                    if (lvtEntity == -1 && insn.getOpcode() == Opcodes.ASTORE) {
                        lvtEntity = (insn as VarInsnNode).var
                    }

                    if (insn.getOpcode() == Opcodes.INVOKESTATIC) {
                        const methodInsn = insn as MethodInsnNode
                        if (methodInsn.owner == 'net/minecraft/world/InteractionResult' && methodInsn.name == ASMAPI.mapMethod('m_19078_') && methodInsn.desc == '(Z)Lnet/minecraft/world/InteractionResult;') {
                            insertBefore = insn
                        }
                    }
                }

                if (lvtEntity < 0 || insertBefore == null) {
                    throw new Error('Failed to patch HangingEntityItem#useOn')
                }

                const target = new InsnList()

                target.add(new VarInsnNode(Opcodes.ALOAD, 1))
                target.add(new VarInsnNode(Opcodes.ALOAD, lvtEntity))
                target.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                    'io/github/noeppi_noeppi/mods/bongo/core/CoreModUtil',
                    'placeHangingEntity', '(Lnet/minecraft/world/item/context/UseOnContext;Lnet/minecraft/world/entity/decoration/HangingEntity;)V'
                ))

                method.instructions.insertBefore(insertBefore, target)

                return method;
            }
        }
    }
}
