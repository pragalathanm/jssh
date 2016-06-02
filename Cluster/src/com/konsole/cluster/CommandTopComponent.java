/*
 * Copyright (C) 2016 Pragalathan M <pragalathanm@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.konsole.cluster;

import com.konsole.term.TerminalFactory;
import com.konsole.term.TerminalTopComponent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.ListSelectionEvent;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.Converter;
import org.jdesktop.beansbinding.ELProperty;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.Mnemonics;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.windows.TopComponent;

/**
 * Top component which displays something.
 *
 * @author Pragalathan M <pragalathanm@gmail.com>
 */
@ConvertAsProperties(
        dtd = "-//com.konsole.cluster//Cluster//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = CommandTopComponent.ID,
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "leftSlidingSide", openAtStartup = true)
@ActionID(category = "Window", id = "com.konsole.cluster.CommandTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_CommandAction",
        preferredID = "CommandTopComponent"
)
@Messages({
    "CTL_CommandAction=Commands",
    "CTL_CommandTopComponent=Commands",
    "HINT_CommandTopComponent=This shows the commands view"
})
public final class CommandTopComponent extends TopComponent {

    public static final String ID = "CommandTopComponent";
    private InstanceContent ic = new InstanceContent();
    private HistoryListModel historyListModel;

    public CommandTopComponent() {
        initComponents();
        setName(Bundle.CTL_CommandTopComponent());
        setToolTipText(Bundle.HINT_CommandTopComponent());
        associateLookup(new AbstractLookup(ic));
        commandTextArea.getInputMap().put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0), "ENTER_ACTION");
        commandTextArea.getInputMap().put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK), "CTRL_L_ACTION");
        commandTextArea.getActionMap().put("ENTER_ACTION", ENTER_KEY_ACTION);
        commandTextArea.getActionMap().put("CTRL_L_ACTION", CTRL_L_ACTION);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateSplitPane(e.getComponent().getSize());
            }
        });
        historyListModel = new HistoryListModel();
        historyList.setModel(historyListModel);
        historyList.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            commandTextArea.setText(historyList.getSelectedValue().toString());
        });
    }

    private void updateSplitPane(Dimension dim) {
        if (dim.height < dim.width && dim.width >= 500) {
            splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
            splitPane.setDividerLocation(dim.width - 300);
        } else {
            splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new BindingGroup();

        splitPane = new JSplitPane();
        jPanel1 = new JPanel();
        jScrollPane2 = new JScrollPane();
        commandTextArea = new JTextArea();
        runButton = new JButton();
        jPanel2 = new JPanel();
        jLabel1 = new JLabel();
        jScrollPane1 = new JScrollPane();
        historyList = new JList();

        setPreferredSize(new Dimension(300, 430));

        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitPane.setOneTouchExpandable(true);

        commandTextArea.setLineWrap(true);
        jScrollPane2.setViewportView(commandTextArea);

        runButton.setIcon(new ImageIcon(getClass().getResource("/com/konsole/cluster/images/exec.png"))); // NOI18N
        Mnemonics.setLocalizedText(runButton, NbBundle.getMessage(CommandTopComponent.class, "CommandTopComponent.runButton.text")); // NOI18N
        runButton.setHorizontalTextPosition(SwingConstants.CENTER);

        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, commandTextArea, ELProperty.create("${text}"), runButton, BeanProperty.create("enabled"));
        binding.setConverter(STRING_TO_BOOLEAN_CONVERTER);
        bindingGroup.addBinding(binding);

        runButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                runButtonActionPerformed(evt);
            }
        });

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(runButton, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2))
        );
        jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(runButton)
                .addGap(0, 75, Short.MAX_VALUE))
        );

        splitPane.setTopComponent(jPanel1);

        jPanel2.setLayout(new BorderLayout());

        jLabel1.setBackground(UIManager.getDefaults().getColor("Button.focus"));
        jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        Mnemonics.setLocalizedText(jLabel1, NbBundle.getMessage(CommandTopComponent.class, "CommandTopComponent.jLabel1.text")); // NOI18N
        jLabel1.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
        jLabel1.setOpaque(true);
        jPanel2.add(jLabel1, BorderLayout.NORTH);

        historyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        historyList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                historyListMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(historyList);

        jPanel2.add(jScrollPane1, BorderLayout.CENTER);

        splitPane.setRightComponent(jPanel2);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(splitPane)
        );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(splitPane, GroupLayout.Alignment.TRAILING)
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void runButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_runButtonActionPerformed
        executeCommand(commandTextArea.getText());
    }//GEN-LAST:event_runButtonActionPerformed

    private void historyListMouseClicked(MouseEvent evt) {//GEN-FIRST:event_historyListMouseClicked
        if (evt.getClickCount() == 2) {
            String command = historyList.getSelectedValue().toString();
            historyListModel.remove(historyList.getSelectedIndex());
            executeCommand(command);
        }
    }//GEN-LAST:event_historyListMouseClicked

    private void executeCommand(String command) {
        executeCommand(command, true);
    }

    private void executeCommand(String command, boolean clearTextField) {
        for (TerminalTopComponent openedTerminal : TerminalFactory.openedTerminals.values()) {
            openedTerminal.execute(command);
        }
        if (clearTextField) {
            commandTextArea.setText("");
        }
        if (!command.trim().isEmpty() && !command.equals("clear")) {
            historyListModel.insert(command);
            historyList.revalidate();
            historyList.updateUI();
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JTextArea commandTextArea;
    private JList historyList;
    private JLabel jLabel1;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPane2;
    private JButton runButton;
    private JSplitPane splitPane;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    @Override
    public void componentOpened() {

    }

    @Override
    public void componentClosed() {
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    private final Converter< String, Boolean> STRING_TO_BOOLEAN_CONVERTER = new Converter<String, Boolean>() {

        @Override
        public Boolean convertForward(String value) {
            return !value.isEmpty();
        }

        @Override
        public String convertReverse(Boolean value) {
            return "";
        }
    };

    private final Action ENTER_KEY_ACTION = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            executeCommand(commandTextArea.getText());
        }
    };
    private final Action CTRL_L_ACTION = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            executeCommand("clear", false);
        }
    };

    class HistoryListModel extends AbstractListModel<String> {

        LinkedList<String> commands = new LinkedList<>();

        @Override
        public int getSize() {
            return commands.size();
        }

        @Override
        public String getElementAt(int index) {
            return commands.get(index);
        }

        public void insert(String command) {
            commands.push(command);
            while (commands.size() > 100) {
                commands.removeLast();
            }
            fireContentsChanged(this, 0, commands.size());
        }

        public void remove(int index) {
            commands.remove(index);
        }
    }
}