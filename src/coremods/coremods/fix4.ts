// Makes spider eyes always edible

import {
    CoreMods,
    MethodNode,
    Opcodes,
    FieldInsnNode, ASMAPI
} from "coremods";

function initializeCoreMod(): CoreMods {
    return {
        'update_sanity': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.world.food.Foods',
                'methodName': '<clinit>',
                'methodDesc': '()V'
            },
            'transformer': (method: MethodNode) => {
                for (let i = 0; i < method.instructions.size(); i++) {
                    const inst = method.instructions.get(i);
                    if (inst != null && inst.getOpcode() == Opcodes.PUTSTATIC) {
                        const fieldInst = inst as FieldInsnNode;
                        if (fieldInst.owner == 'net/minecraft/world/food/Foods' 
                          && fieldInst.name == ASMAPI.mapField('f_38806_')) {
                            const insertBefore = inst.getPrevious()
                            if (insertBefore != null) {
                                method.instructions.insertBefore(insertBefore, ASMAPI.buildMethodCall(
                                    'net/minecraft/world/food/FoodProperties$Builder',
                                    ASMAPI.mapMethod('m_38765_'),
                                    '()Lnet/minecraft/world/food/FoodProperties$Builder;',
                                    ASMAPI.MethodType.VIRTUAL
                                ))
                                break
                            }
                        }
                    }
                }
                return method;
            }
        }
    }
}