// Fixes https://github.com/pRottinghuis/BumpkinBatch/issues/1

import {
    AbstractInsnNode,
    ASMAPI,
    CoreMods,
    InsnList, JumpInsnNode,
    LabelNode,
    MethodInsnNode,
    MethodNode,
    Opcodes,
    TypeInsnNode,
    VarInsnNode
} from "coremods";

function initializeCoreMod(): CoreMods {
    return {
        'world_load': {
            'target': {
                'type': 'METHOD',
                'class': 'team.rusty.util.worldgen.structure.SimpleStructureRegistry',
                'methodName': 'worldLoad',
                'methodDesc': '(Lnet/minecraftforge/event/world/WorldEvent$Load;)V'
            },
            'transformer': (method: MethodNode) => {
                let lvtIdx = -1;
                let insertAfter: AbstractInsnNode | null = null;
                let insertEnd: AbstractInsnNode | null = null;

                for (let i = 0; i < method.instructions.size(); i++) {
                    const inst = method.instructions.get(i);
                    if (lvtIdx < 0 && inst != null && inst.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                        const invokeInst = inst as MethodInsnNode;
                        if (invokeInst.owner == 'net/minecraft/world/level/levelgen/StructureSettings' 
                          && invokeInst.name == ASMAPI.mapMethod('m_64590_') 
                          && invokeInst.desc == '()Ljava/util/Map;') {
                            const next = inst.getNext();
                            if (next != null && next.getOpcode() == Opcodes.ASTORE) {
                                lvtIdx = (next as VarInsnNode).var;
                                insertAfter = next;
                            }
                        }
                    }
                    if (inst != null && inst.getOpcode() == Opcodes.RETURN) {
                        insertEnd = inst;
                    }
                }
                // Silently ignore
                if (lvtIdx < 0 || insertAfter == null || insertEnd == null) return method;

                const label = new LabelNode();
                const target = new InsnList();
                target.add(new VarInsnNode(Opcodes.ALOAD, lvtIdx));
                target.add(new TypeInsnNode(Opcodes.INSTANCEOF, 'com/google/common/collect/ImmutableMap'));
                target.add(new JumpInsnNode(Opcodes.IFNE, label));

                method.instructions.insert(insertAfter, target);
                method.instructions.insertBefore(insertEnd, label);

                return method;
            }
        }
    }
}

