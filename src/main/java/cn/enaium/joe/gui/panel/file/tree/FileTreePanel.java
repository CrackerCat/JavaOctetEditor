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

package cn.enaium.joe.gui.panel.file.tree;

import cn.enaium.joe.JavaOctetEditor;
import cn.enaium.joe.dialog.FieldDialog;
import cn.enaium.joe.dialog.MethodDialog;
import cn.enaium.joe.event.listener.FileTabbedSelectListener;
import cn.enaium.joe.gui.component.MemberList;
import cn.enaium.joe.gui.layout.HalfLayout;
import cn.enaium.joe.gui.panel.file.tabbed.tab.classes.ClassTabPanel;
import cn.enaium.joe.gui.panel.LeftPanel;
import cn.enaium.joe.jar.Jar;
import cn.enaium.joe.util.JTreeUtil;
import cn.enaium.joe.util.LangUtil;
import cn.enaium.joe.util.OpcodeUtil;
import cn.enaium.joe.util.Pair;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author Enaium
 * @since 1.2.0
 */
public class FileTreePanel extends JPanel {
    public FileTreePanel() {
        super(new BorderLayout());

        JPanel jPanel = new JPanel(new HalfLayout(HalfLayout.TOP_AND_BOTTOM));
        jPanel.add(new JPanel(new BorderLayout()) {{
            add(new JPanel(new BorderLayout()) {{
                setBorder(new EmptyBorder(0, 0, 5, 0));
                add(new JTextField() {{
                    putClientProperty("JTextField.placeholderText", LangUtil.i18n("menu.search"));
                    JTextField jTextField = this;
                    addKeyListener(new KeyAdapter() {
                        @Override
                        public void keyPressed(KeyEvent e) {
                            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                                Jar jar = JavaOctetEditor.getInstance().getJar();
                                if (jar != null) {
                                    if (!jTextField.getText().replace(" ", "").isEmpty()) {
                                        Jar searchedJar = jar.copy();

                                        searchedJar.classes = searchedJar.classes.entrySet().stream().filter(stringClassNodeEntry -> {
                                            String key = stringClassNodeEntry.getKey();

                                            if (!key.contains("/")) {
                                                key = key.substring(key.lastIndexOf("/") + 1);
                                            }

                                            return key.toLowerCase(Locale.ROOT).contains(jTextField.getText().toLowerCase(Locale.ROOT));
                                        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                                        searchedJar.resources = searchedJar.resources.entrySet().stream().filter(stringEntry -> {
                                            String key = stringEntry.getKey();
                                            if (!key.contains("/")) {
                                                key = key.substring(key.lastIndexOf("/") + 1);
                                            }
                                            return key.toLowerCase(Locale.ROOT).contains(jTextField.getText().toLowerCase(Locale.ROOT));
                                        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

                                        JavaOctetEditor.getInstance().fileTree.refresh(searchedJar);
                                        JTreeUtil.setTreeExpandedState(JavaOctetEditor.getInstance().fileTree, true);
                                    } else {
                                        JavaOctetEditor.getInstance().fileTree.refresh(jar);
                                    }
                                }
                            }
                        }
                    });
                }}, BorderLayout.CENTER);
            }}, BorderLayout.NORTH);
            add(new JScrollPane(JavaOctetEditor.getInstance().fileTree), BorderLayout.CENTER);
        }}, HalfLayout.TOP);
        jPanel.add(new JScrollPane() {{
            JLabel noMember = new JLabel(LangUtil.i18n("class.info.noMember"), SwingConstants.CENTER);
            setViewportView(noMember);
            JavaOctetEditor.getInstance().event.register(LeftPanel.BottomToggleButtonListener.class, (Consumer<LeftPanel.BottomToggleButtonListener>) listener -> {
                if (listener.getType() == LeftPanel.BottomToggleButtonListener.Type.MEMBER) {
                    setVisible(listener.isSelect());
                    jPanel.validate();
                }
            });

            JavaOctetEditor.getInstance().event.register(FileTabbedSelectListener.class, (Consumer<FileTabbedSelectListener>) listener -> {
                Component select = listener.getSelect();
                if (select instanceof ClassTabPanel) {
                    setViewportView(new MemberList(((ClassTabPanel) select).getClassNode()));
                }else {
                    setViewportView(noMember);
                }
            });
            setVisible(false);
        }}, HalfLayout.BOTTOM);
        add(jPanel, BorderLayout.CENTER);
    }
}
