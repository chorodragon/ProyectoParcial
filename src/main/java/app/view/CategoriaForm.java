// src/main/java/app/view/CategoriaForm.java
package app.view;

import app.core.ValidationUtil;
import app.dao.CategoriaDAO;
import app.model.Categoria;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CategoriaForm {
    public JPanel panelPrincipal;
    private JTextField txtNombre;
    private JComboBox<String> cboEstado;
    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnCargar;
    private JTable tblCategorias;

    private final CategoriaDAO categoriaDAO = new CategoriaDAO();
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Nombre", "Estado"}, 0
    );

    private Integer selectedId = null;

    public CategoriaForm() {
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
        
        // Estado
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
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
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);
        
        // Tabla
        tblCategorias = new JTable(model);
        tblCategorias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tblCategorias);
        scrollPane.setPreferredSize(new Dimension(500, 300));
        
        panelPrincipal.add(formPanel, BorderLayout.NORTH);
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);
        
        panelPrincipal.setPreferredSize(new Dimension(600, 500));
    }

    private void setupEventListeners() {
        btnGuardar.addActionListener(e -> onGuardar());
        btnActualizar.addActionListener(e -> onActualizar());
        btnEliminar.addActionListener(e -> onEliminar());
        btnCargar.addActionListener(e -> cargarTabla());
        
        tblCategorias.getSelectionModel().addListSelectionListener(this::onTableSelection);
    }

    private void onTableSelection(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) return;
        int row = tblCategorias.getSelectedRow();
        if (row == -1) {
            selectedId = null;
            return;
        }
        
        selectedId = Integer.parseInt(model.getValueAt(row, 0).toString());
        txtNombre.setText(model.getValueAt(row, 1).toString());
        
        String estTxt = model.getValueAt(row, 2).toString();
        cboEstado.setSelectedIndex("Activo".equalsIgnoreCase(estTxt) ? 0 : 1);
    }

    private void onGuardar() {
        String nombre = txtNombre.getText().trim();
        
        String error = ValidationUtil.validateRequired(nombre, "Nombre");
        if (error != null) {
            ValidationUtil.showError(error);
            txtNombre.requestFocus();
            return;
        }

        int estado = (cboEstado.getSelectedIndex() == 0) ? 1 : 0;

        try {
            Categoria c = new Categoria(nombre, estado);
            categoriaDAO.insertar(c);
            ValidationUtil.showSuccess("Categoría guardada exitosamente");
            limpiarFormulario();
            cargarTabla();
        } catch (Exception ex) {
            ValidationUtil.showError("Error al guardar la categoría: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void onActualizar() {
        if (selectedId == null) {
            ValidationUtil.showError("Seleccione una categoría para actualizar");
            return;
        }
        
        String nombre = txtNombre.getText().trim();
        
        String error = ValidationUtil.validateRequired(nombre, "Nombre");
        if (error != null) {
            ValidationUtil.showError(error);
            txtNombre.requestFocus();
            return;
        }

        if (!ValidationUtil.showConfirmation("¿Está seguro de actualizar esta categoría?")) {
            return;
        }

        int estado = (cboEstado.getSelectedIndex() == 0) ? 1 : 0;

        try {
            Categoria c = new Categoria(selectedId, nombre, estado);
            boolean ok = categoriaDAO.actualizar(c);
            if (ok) {
                ValidationUtil.showSuccess("Categoría actualizada exitosamente");
                cargarTabla();
                seleccionarFilaPorId(selectedId);
            } else {
                ValidationUtil.showError("No se pudo actualizar la categoría");
            }
        } catch (Exception ex) {
            ValidationUtil.showError("Error al actualizar la categoría: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void onEliminar() {
        if (selectedId == null) {
            ValidationUtil.showError("Seleccione una categoría para eliminar");
            return;
        }

        if (!ValidationUtil.showConfirmation("¿Está seguro de eliminar esta categoría?\nEsta acción marcará la categoría como inactiva.")) {
            return;
        }

        try {
            boolean ok = categoriaDAO.eliminarLogico(selectedId);
            if (ok) {
                ValidationUtil.showSuccess("Categoría eliminada exitosamente");
                limpiarFormulario();
                cargarTabla();
            } else {
                ValidationUtil.showError("No se pudo eliminar la categoría");
            }
        } catch (Exception ex) {
            ValidationUtil.showError("Error al eliminar la categoría: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void cargarTabla() {
        try {
            List<Categoria> lista = categoriaDAO.listar();
            model.setRowCount(0);
            for (Categoria c : lista) {
                model.addRow(new Object[]{
                        c.getId(),
                        c.getNombre(),
                        c.getEstado() == 1 ? "Activo" : "Inactivo"
                });
            }
        } catch (Exception ex) {
            ValidationUtil.showError("Error al cargar categorías: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void limpiarFormulario() {
        txtNombre.setText("");
        cboEstado.setSelectedIndex(0);
        tblCategorias.clearSelection();
        selectedId = null;
    }

    private void seleccionarFilaPorId(Integer id) {
        if (id == null) return;
        for (int i = 0; i < model.getRowCount(); i++) {
            Object val = model.getValueAt(i, 0);
            if (val != null && Integer.parseInt(val.toString()) == id) {
                tblCategorias.setRowSelectionInterval(i, i);
                break;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Gestión de Categorías");
            f.setContentPane(new CategoriaForm().panelPrincipal);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.pack();
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}