// src/main/java/app/view/ClienteForm.java
package app.view;

import app.core.ValidationUtil;
import app.dao.ClienteDAO;
import app.model.Cliente;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ClienteForm {
    public JPanel panelPrincipal;
    private JTextField txtNombre;
    private JTextField txtNit;
    private JTextField txtTelefono;
    private JComboBox<String> cboEstado;
    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnCargar;
    private JTable tblClientes;
    private JTextField txtBuscar;

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Nombre", "NIT", "Teléfono", "Estado"}, 0
    );

    private Integer selectedId = null;

    public ClienteForm() {
        initializeComponents();
        setupEventListeners();
        cargarTabla();
    }

    private void initializeComponents() {
        panelPrincipal = new JPanel(new BorderLayout());
        
        // Panel de formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Nombre
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Nombre:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtNombre = new JTextField(20);
        formPanel.add(txtNombre, gbc);
        
        // NIT
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("NIT:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtNit = new JTextField(20);
        formPanel.add(txtNit, gbc);
        
        // Teléfono
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Teléfono:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtTelefono = new JTextField(20);
        formPanel.add(txtTelefono, gbc);
        
        // Estado
        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Estado:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        cboEstado = new JComboBox<>(new String[]{"1 - Activo", "0 - Inactivo"});
        formPanel.add(cboEstado, gbc);
        
        // Botones
        JPanel buttonPanel = new JPanel(new FlowLayout());
        btnGuardar = new JButton("Guardar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnCargar = new JButton("Cargar");
        
        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnActualizar);
        buttonPanel.add(btnEliminar);
        buttonPanel.add(btnCargar);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);
        
        // Panel de búsqueda
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Buscar por nombre:"));
        txtBuscar = new JTextField(20);
        searchPanel.add(txtBuscar);
        
        // Tabla
        tblClientes = new JTable(model);
        tblClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tblClientes);
        scrollPane.setPreferredSize(new Dimension(600, 300));
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        panelPrincipal.add(formPanel, BorderLayout.NORTH);
        panelPrincipal.add(centerPanel, BorderLayout.CENTER);
        
        panelPrincipal.setPreferredSize(new Dimension(700, 600));
    }

    private void setupEventListeners() {
        btnGuardar.addActionListener(e -> onGuardar());
        btnActualizar.addActionListener(e -> onActualizar());
        btnEliminar.addActionListener(e -> onEliminar());
        btnCargar.addActionListener(e -> cargarTabla());
        
        tblClientes.getSelectionModel().addListSelectionListener(this::onTableSelection);
        
        // Búsqueda en tiempo real
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                buscarClientes();
            }
        });
    }

    private void onTableSelection(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) return;
        int row = tblClientes.getSelectedRow();
        if (row == -1) {
            selectedId = null;
            return;
        }
        
        selectedId = Integer.parseInt(model.getValueAt(row, 0).toString());
        txtNombre.setText(model.getValueAt(row, 1).toString());
        txtNit.setText(model.getValueAt(row, 2) != null ? model.getValueAt(row, 2).toString() : "");
        txtTelefono.setText(model.getValueAt(row, 3) != null ? model.getValueAt(row, 3).toString() : "");
        
        String estTxt = model.getValueAt(row, 4).toString();
        cboEstado.setSelectedIndex("Activo".equalsIgnoreCase(estTxt) ? 0 : 1);
    }

    private void onGuardar() {
        String nombre = txtNombre.getText().trim();
        String nit = txtNit.getText().trim();
        String telefono = txtTelefono.getText().trim();
        
        // Validaciones
        String error = ValidationUtil.validateRequired(nombre, "Nombre");
        if (error != null) {
            ValidationUtil.showError(error);
            txtNombre.requestFocus();
            return;
        }
        
        if (!ValidationUtil.isValidNit(nit)) {
            ValidationUtil.showError("El NIT tiene un formato inválido");
            txtNit.requestFocus();
            return;
        }
        
        if (!ValidationUtil.isValidTelefono(telefono)) {
            ValidationUtil.showError("El teléfono tiene un formato inválido");
            txtTelefono.requestFocus();
            return;
        }

        int estado = (cboEstado.getSelectedIndex() == 0) ? 1 : 0;

        try {
            Cliente c = new Cliente(nombre, nit.isEmpty() ? null : nit, telefono.isEmpty() ? null : telefono, estado);
            clienteDAO.insertar(c);
            ValidationUtil.showSuccess("Cliente guardado exitosamente");
            limpiarFormulario();
            cargarTabla();
        } catch (Exception ex) {
            ValidationUtil.showError("Error al guardar el cliente: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void onActualizar() {
        if (selectedId == null) {
            ValidationUtil.showError("Seleccione un cliente para actualizar");
            return;
        }
        
        String nombre = txtNombre.getText().trim();
        String nit = txtNit.getText().trim();
        String telefono = txtTelefono.getText().trim();
        
        // Validaciones
        String error = ValidationUtil.validateRequired(nombre, "Nombre");
        if (error != null) {
            ValidationUtil.showError(error);
            txtNombre.requestFocus();
            return;
        }
        
        if (!ValidationUtil.isValidNit(nit)) {
            ValidationUtil.showError("El NIT tiene un formato inválido");
            txtNit.requestFocus();
            return;
        }
        
        if (!ValidationUtil.isValidTelefono(telefono)) {
            ValidationUtil.showError("El teléfono tiene un formato inválido");
            txtTelefono.requestFocus();
            return;
        }

        if (!ValidationUtil.showConfirmation("¿Está seguro de actualizar este cliente?")) {
            return;
        }

        int estado = (cboEstado.getSelectedIndex() == 0) ? 1 : 0;

        try {
            Cliente c = new Cliente(selectedId, nombre, nit.isEmpty() ? null : nit, telefono.isEmpty() ? null : telefono, estado);
            boolean ok = clienteDAO.actualizar(c);
            if (ok) {
                ValidationUtil.showSuccess("Cliente actualizado exitosamente");
                cargarTabla();
                seleccionarFilaPorId(selectedId);
            } else {
                ValidationUtil.showError("No se pudo actualizar el cliente");
            }
        } catch (Exception ex) {
            ValidationUtil.showError("Error al actualizar el cliente: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void onEliminar() {
        if (selectedId == null) {
            ValidationUtil.showError("Seleccione un cliente para eliminar");
            return;
        }

        if (!ValidationUtil.showConfirmation("¿Está seguro de eliminar este cliente?\nEsta acción marcará el cliente como inactivo.")) {
            return;
        }

        try {
            boolean ok = clienteDAO.eliminarLogico(selectedId);
            if (ok) {
                ValidationUtil.showSuccess("Cliente eliminado exitosamente");
                limpiarFormulario();
                cargarTabla();
            } else {
                ValidationUtil.showError("No se pudo eliminar el cliente");
            }
        } catch (Exception ex) {
            ValidationUtil.showError("Error al eliminar el cliente: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void cargarTabla() {
        try {
            List<Cliente> lista = clienteDAO.listar();
            model.setRowCount(0);
            for (Cliente c : lista) {
                model.addRow(new Object[]{
                        c.getId(),
                        c.getNombre(),
                        c.getNit(),
                        c.getTelefono(),
                        c.getEstado() == 1 ? "Activo" : "Inactivo"
                });
            }
        } catch (Exception ex) {
            ValidationUtil.showError("Error al cargar clientes: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void buscarClientes() {
        String termino = txtBuscar.getText().trim();
        if (termino.isEmpty()) {
            cargarTabla();
            return;
        }
        
        try {
            List<Cliente> lista = clienteDAO.buscarPorNombre(termino);
            model.setRowCount(0);
            for (Cliente c : lista) {
                model.addRow(new Object[]{
                        c.getId(),
                        c.getNombre(),
                        c.getNit(),
                        c.getTelefono(),
                        c.getEstado() == 1 ? "Activo" : "Inactivo"
                });
            }
        } catch (Exception ex) {
            ValidationUtil.showError("Error al buscar clientes: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void limpiarFormulario() {
        txtNombre.setText("");
        txtNit.setText("");
        txtTelefono.setText("");
        cboEstado.setSelectedIndex(0);
        tblClientes.clearSelection();
        selectedId = null;
    }

    private void seleccionarFilaPorId(Integer id) {
        if (id == null) return;
        for (int i = 0; i < model.getRowCount(); i++) {
            Object val = model.getValueAt(i, 0);
            if (val != null && Integer.parseInt(val.toString()) == id) {
                tblClientes.setRowSelectionInterval(i, i);
                break;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Gestión de Clientes");
            f.setContentPane(new ClienteForm().panelPrincipal);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.pack();
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}