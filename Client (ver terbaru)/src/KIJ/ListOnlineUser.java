/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package KIJ;

import javax.swing.DefaultListModel;

/**
 *
 * @author alif.sip
 */
public class ListOnlineUser extends javax.swing.JFrame {

    /**
     * Creates new form test
     */
    public ListOnlineUser() {
        initComponents();
    }
    
    private Client client;
    public String username;
    public String ip;
    public int port;
    DefaultListModel online = new DefaultListModel();
    
    public void Connect()
    {
        client = new Client(ip,port,username, this);
        if(!client.start()) return;   
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        dconn = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        starChat = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        chatList = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Desain daftar online Client.png"))); // NOI18N
        jLabel2.setText("jLabel2");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        dconn.setBackground(new java.awt.Color(255, 0, 51));
        dconn.setText("Disconnect");
        dconn.setToolTipText("");
        dconn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dconnActionPerformed(evt);
            }
        });
        getContentPane().add(dconn, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 220, 90, -1));

        jButton1.setText("Refresh");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 220, 110, -1));

        starChat.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        starChat.setText("Start Chat");
        starChat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                starChatActionPerformed(evt);
            }
        });
        getContentPane().add(starChat, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 50, 90, 160));

        chatList.setBackground(new java.awt.Color(204, 204, 204));
        chatList.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jScrollPane1.setViewportView(chatList);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 50, 220, 160));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image/Desain daftar online Client.png"))); // NOI18N
        jLabel1.setText("jLabel1");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 470, 270));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void starChatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_starChatActionPerformed
        // TODO add your handling code here:
        if(chatList.getSelectedValue() != null)
        client.NewChat((String) chatList.getSelectedValue(),username);   
    }//GEN-LAST:event_starChatActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:

        client.sendMessage("WHO\r\n");

    }//GEN-LAST:event_jButton1ActionPerformed

    private void dconnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dconnActionPerformed
        // TODO add your handling code here:
        client.sendMessage("BYE\r\n");
        this.dispose();
    }//GEN-LAST:event_dconnActionPerformed
    
    void hapus()
    {
        online.clear();
    }
    void updateOnlineList(String str)
    { 
        online.addElement(str);
        chatList.setModel(online);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ListOnlineUser.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ListOnlineUser.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ListOnlineUser.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ListOnlineUser.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ListOnlineUser().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList chatList;
    private javax.swing.JButton dconn;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton starChat;
    // End of variables declaration//GEN-END:variables
}
