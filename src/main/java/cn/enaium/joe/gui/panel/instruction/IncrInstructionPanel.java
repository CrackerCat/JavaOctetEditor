/*
 * Copyright 2022 Enaium
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.enaium.joe.gui.panel.instruction;

import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnList;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Enaium
 * @since 0.8.0
 */
public class IncrInstructionPanel extends AbstractInstructionPanel {
    public IncrInstructionPanel(IincInsnNode instruction, InsnList instructions) {
        super(instruction, instructions);
        JSpinner varIndex = new JSpinner();
        varIndex.setValue(instruction.var);
        addComponent(new JLabel("Var Index:"), varIndex);
        JSpinner incr = new JSpinner();
        incr.setValue(instruction.incr);
        addComponent(new JLabel("Incr:"), incr);
        setConfirm(() -> {
            instructions.set(instruction, new IincInsnNode(getOpcode(), Integer.parseInt(varIndex.getValue().toString())));
            return true;
        });
    }

    @Override
    public List<String> getOpcodes() {
        return Collections.singletonList("IINC");
    }
}
