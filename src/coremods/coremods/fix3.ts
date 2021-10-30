// Limits sanity for lost in the mist to above 56
// This implicitly disables the fog world

import {
    CoreMods,
    MethodNode,
    Opcodes,
    InsnList,
    VarInsnNode,
    LdcInsnNode,
    InsnNode, LabelNode, JumpInsnNode
} from "coremods";

function initializeCoreMod(): CoreMods {
    return {
        'update_sanity': {
            'target': {
                'type': 'METHOD',
                'class': 'melonslise.spook.common.capability.Sanity',
                'methodName': 'set',
                'methodDesc': '(F)V'
            },
            'transformer': (method: MethodNode) => {
                const target = new InsnList();
                const label = new LabelNode();
                target.add(new VarInsnNode(Opcodes.FLOAD, 1));
                target.add(new LdcInsnNode(56));
                target.add(new InsnNode(Opcodes.I2F));
                target.add(new InsnNode(Opcodes.FCMPG));
                target.add(new JumpInsnNode(Opcodes.IFGE, label));
                target.add(new LdcInsnNode(56));
                target.add(new InsnNode(Opcodes.I2F));
                target.add(new VarInsnNode(Opcodes.FSTORE, 1));
                target.add(label);
                method.instructions.insert(target);
                return method;
            }
        }
    }
}