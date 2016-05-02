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

import com.konsole.cluster.cookie.ClusterCookie;
import com.konsole.cluster.host.Host;
import com.konsole.cluster.nodes.factory.ClusterChildFactory;
import com.konsole.term.TerminalFactory;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ProxyLookup;
import org.openide.windows.TopComponent;

/**
 *
 * @author Pragalathan M <pragalathanm@gmail.com>
 */
public class ClusterPanel extends TopComponent implements ExplorerManager.Provider {

    private InstanceContent ic = new InstanceContent();
    private ExplorerManager em = new ExplorerManager();
    private ClusterChildFactory clusterChildFactory = new ClusterChildFactory(new ArrayList<Cluster>());
    private Lookup.Result<Cluster> clusterResult;
    private Cluster selectedCluster;
    private static ClusterPanel instance;
    private static final Logger LOG = Logger.getLogger(ClusterPanel.class.getName());

    public static ClusterPanel getInstance() {
        if (instance == null) {
            instance = new ClusterPanel();
        }
        return instance;
    }

    public ClusterPanel() {
        initComponents();

        Lookup lookup = ExplorerUtils.createLookup(em, this.getActionMap());
        associateLookup(new ProxyLookup(lookup, new AbstractLookup(ic)));
        AbstractNode root = new AbstractNode(Children.create(clusterChildFactory, true));
        em.setRootContext(root);
        clusterResult = lookup.lookupResult(Cluster.class);
        clusterResult.addLookupListener(new LookupListener() {

            @Override
            public void resultChanged(LookupEvent ev) {
                Collection<? extends Cluster> allInstances = clusterResult.allInstances();
                if (allInstances.isEmpty()) {
                    ic.remove(clusterCookie);
                    selectedCluster = null;
                } else {
                    if (getLookup().lookup(ClusterCookie.class) == null) {
                        ic.add(clusterCookie);
                    }
                    selectedCluster = allInstances.iterator().next();
                }
            }
        });
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return em;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        choiceView1 = new org.openide.explorer.view.ChoiceView();
        jLabel1 = new javax.swing.JLabel();

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(ClusterPanel.class, "ClusterPanel.jLabel1.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(4, 4, 4)
                .addComponent(choiceView1, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(choiceView1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    public void addCluster(String name) {
        if (name != null && !name.isEmpty()) {
            Cluster cluster = new Cluster(name);
            clusterChildFactory.addEntry(cluster);
        }
    }

    public void loadClusters() {
        LOG.info("loading clusters...");
        try {
            List<Cluster> clusters = StoreManager.getClusters();
            clusterChildFactory.addEntry(clusters);
        } catch (IOException | ClassNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void storeClusters() {
        try {
            StoreManager.setClusters(clusterChildFactory.getEntries());
            LOG.info("Storing cluster details...");
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.openide.explorer.view.ChoiceView choiceView1;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
    private ClusterCookie clusterCookie = new ClusterCookie() {

        @Override
        public void removeCluster() {
            clusterChildFactory.removeEntry(em.getSelectedNodes());
        }

        @Override
        public void open() {
            for (Host host : selectedCluster.getHosts()) {
                TerminalFactory.newTerminalTopComponent(host.getName()).execute("ssh " + host.getIpAddress());
            }
        }

        @Override
        public void addHost(String... hostNames) {
            for (String name : hostNames) {
                selectedCluster.getHosts().add(new Host(name));
            }

            final Node[] selectedNodes = em.getSelectedNodes();
            try {
                em.setSelectedNodes(new Node[0]);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            em.setSelectedNodes(selectedNodes);
                        } catch (PropertyVetoException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                });

            } catch (PropertyVetoException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        @Override
        public void close() {
            TerminalFactory.closeAll();
        }
    };

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
}
