"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var coremods_1 = require("coremods");
function initializeCoreMod() {
    return {
        hanging_entity: {
            target: {
                type: 'METHOD',
                class: 'net.minecraft.world.item.HangingEntityItem',
                methodName: 'm_6225_',
                methodDesc: '(Lnet/minecraft/world/item/context/UseOnContext;)Lnet/minecraft/world/InteractionResult;'
            },
            transformer: function (method) {
                var lvtEntity = -2;
                var insertBefore = null;
                for (var i = 0; i < method.instructions.size(); i++) {
                    var insn = method.instructions.get(i);
                    if (insn == null)
                        continue;
                    if (lvtEntity == -2 && insn.getOpcode() == coremods_1.Opcodes.INVOKESPECIAL) {
                        var methodInsn = insn;
                        if (methodInsn.owner == 'net/minecraft/world/entity/decoration/ItemFrame' && methodInsn.name == '<init>') {
                            lvtEntity = -1;
                        }
                    }
                    if (lvtEntity == -1 && insn.getOpcode() == coremods_1.Opcodes.ASTORE) {
                        lvtEntity = insn.var;
                    }
                    if (insn.getOpcode() == coremods_1.Opcodes.INVOKESTATIC) {
                        var methodInsn = insn;
                        if (methodInsn.owner == 'net/minecraft/world/InteractionResult' && methodInsn.name == coremods_1.ASMAPI.mapMethod('m_19078_') && methodInsn.desc == '(Z)Lnet/minecraft/world/InteractionResult;') {
                            insertBefore = insn;
                        }
                    }
                }
                if (lvtEntity < 0 || insertBefore == null) {
                    throw new Error('Failed to patch HangingEntityItem#useOn');
                }
                var target = new coremods_1.InsnList();
                target.add(new coremods_1.VarInsnNode(coremods_1.Opcodes.ALOAD, 1));
                target.add(new coremods_1.VarInsnNode(coremods_1.Opcodes.ALOAD, lvtEntity));
                target.add(new coremods_1.MethodInsnNode(coremods_1.Opcodes.INVOKESTATIC, 'io/github/noeppi_noeppi/mods/bongo/core/CoreModUtil', 'placeHangingEntity', '(Lnet/minecraft/world/item/context/UseOnContext;Lnet/minecraft/world/entity/decoration/HangingEntity;)V'));
                method.instructions.insertBefore(insertBefore, target);
                return method;
            }
        }
    };
}
