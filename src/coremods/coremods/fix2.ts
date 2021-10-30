// Redirects addition of potion effects from badbone, so we can filter them

import { CoreMods, MethodNode, Opcodes, MethodInsnNode, ASMAPI } from "coremods";

function initializeCoreMod(): CoreMods {
    return {
        'update_player': {
            'target': {
                'type': 'METHOD',
                'class': 'subaraki.badbone.events.PlayerUpdateEvent',
                'methodName': 'playerUpdate',
                'methodDesc': '(Lnet/minecraftforge/event/TickEvent$PlayerTickEvent;)V'
            },
            'transformer': (method: MethodNode) => {
                const instructionsToReplace = []
                for (let i = 0; i < method.instructions.size(); i++) {
                    const inst = method.instructions.get(i);
                    if (inst != null && inst.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                        const invokeInst = inst as MethodInsnNode;
                        if (invokeInst.owner == 'net/minecraft/server/level/ServerPlayer' 
                          && invokeInst.name == ASMAPI.mapMethod('m_7292_') 
                          && invokeInst.desc == '(Lnet/minecraft/world/effect/MobEffectInstance;)Z') {
                            instructionsToReplace.push(inst)
                        }
                    }
                }
                for (const inst of instructionsToReplace) {
                    method.instructions.set(inst, ASMAPI.buildMethodCall(
                        'io/github/noeppi_noeppi/mods/bongo/SpookyHooks', 'addBadBoneEffect',
                        '(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/effect/MobEffectInstance;)Z',
                        ASMAPI.MethodType.STATIC
                    ));
                }
                return method
            }
        }
    }
}