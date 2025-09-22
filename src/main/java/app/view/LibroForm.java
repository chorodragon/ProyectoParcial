// src/main/java/app/view/LibroForm.java (Actualizado)
package app.view;

import app.core.ValidationUtil;
import app.dao.AutorDAO;
import app.dao.CategoriaDAO;
import app.dao.LibroDAO;
import app.model.Autor;
import app.model.Categoria;
import app.model.ComboItem;
import app.model.Libro;
import app.model.LibroConAutor;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LibroForm {
    public JPanel panelPrincipal;
    private JTextField txtNombre;
    private JTextField txtAnio;
    private JComboBox<ComboItem> cboAutor;
    private JComboBox<ComboItem> cboCategoria;
    private JComboBox<String> cboEstado;
    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnCargar;
    private JTable tblLibros;
    
    // Filtros
    private JTextField txtFiltroNombre;
    private JComboBox<ComboItem> cboFiltroCategoria;

    private final AutorDAO autorDAO = new AutorDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();
    private final LibroDAO libroDAO = new LibroDAO();

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Nombre", "Año", "Autor", "Categoría", "Estado"}, 0
    );

    private Integer selectedId = null;

    public LibroForm() {
        initializeComponents();
        setupEventListeners();
        cargarAutoresEnCombo();
        cargarCategoriasEnCombo();
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
        
        // Año
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Año:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtAnio = new JTextField(20);
        formPanel.add(txtAnio, gbc);
        
        // Autor
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Autor:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        cboAutor = new JComboBox<>();
        formPanel.add(cboAutor, gbc);
        
        // Categoría
        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Categoría:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        cboCategoria = new JComboBox<>();
        formPanel.add(cboCategoria, gbc);
        
        // Estado
        gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
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
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);
        
        // Panel de filtros
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        filterPanel.add(new JLabel("Filtrar por nombre:"));
        txtFiltroNombre = new JTextField(15);
        filterPanel.add(txtFiltroNombre);
        
        filterPanel.add(new JLabel("Por categoría:"));
        cboFiltroCategoria = new JComboBox<>();
        filterPanel.add(cboFiltroCategoria);
        
        // Tabla
        tblLibros = new JTable(model);
        tblLibros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tblLibros);
        scrollPane.setPreferredSize(new Dimension(800, 300));
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(filterPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        panelPrincipal.add(formPanel, BorderLayout.NORTH);
        panelPrincipal.add(centerPanel, BorderLayout.CENTER);
        
        panelPrincipal.setPreferredSize(new Dimension(900, 700));
    }

    private void setupEventListeners() {
        btnGuardar.addActionListener(e -> onGuardar());
        btnActualizar.addActionListener(e -> onActualizar());
        btnEliminar.addActionListener(e -> onEliminar());
        btnCargar.addActionListener(e -> cargarTabla());

        tblLibros.getSelectionModel().addListSelectionListener(this::onTableSelection);
        
        // Filtros en tiempo real
        txtFiltroNombre.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                aplicarFiltros();
            }
        });
        
        cboFiltroCategoria.addActionListener(e -> aplicarFiltros());
    }

    private void onTableSelection(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) return;
        int row = tblLibros.getSelectedRow();
        if (row == -1) {
            selectedId = null;
            return;
        }

        selectedId = Integer.parseInt(model.getValueAt(row, 0).toString());
        txtNombre.setText(model.getValueAt(row, 1).toString());
        txtAnio.setText(model.getValueAt(row, 2).toString());

        // Seleccionar autor en combo
        String autorNombre = model.getValueAt(row, 3).toString();
        seleccionarAutorPorNombre(autorNombre);
        
        // Seleccionar categoría en combo
        String categoriaNombre = model.getValueAt(row, 4).toString();
        seleccionarCategoriaPorNombre(categoriaNombre);

        String estTxt = model.getValueAt(row, 5).toString();
        cboEstado.setSelectedIndex("Activo".equalsIgnoreCase(estTxt) ? 0 : 1);
    }

    private void cargarAutoresEnCombo() {
        try {
            cboAutor.removeAllItems();
            List<Autor> autores = autorDAO.listarActivos();
            for (Autor a : autores) {
                cboAutor.addItem(new ComboItem(a.getId(), a.getNombre()));
            }
        } catch (Exception ex) {
            ValidationUtil.showError("Error al cargar autores: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void cargarCategoriasEnCombo() {
        try {
            cboCategoria.removeAllItems();
            cboFiltroCategoria.removeAllItems();
            cboFiltroCategoria.addItem(new ComboItem(-1, "Todas las categorías"));
            
            List<Categoria> categorias = categoriaDAO.listarActivas();
            for (Categoria c : categorias) {
                ComboItem item = new ComboItem(c.getId(), c.getNombre());
                cboCategoria.addItem(item);
                cboFiltroCategoria.addItem(item);
            }
        } catch (Exception ex) {
            ValidationUtil.showError("Error al cargar categorías: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void seleccionarAutorPorNombre(String nombre) {
        for (int i = 0; i < cboAutor.getItemCount(); i++) {
            ComboItem item = cboAutor.getItemAt(i);
            if (item.getLabel().equalsIgnoreCase(nombre)) {
                cboAutor.setSelectedIndex(i);
                return;
            }
        }
    }

    private void seleccionarCategoriaPorNombre(String nombre) {
        for (int i = 0; i < cboCategoria.getItemCount(); i++) {
            ComboItem item = cboCategoria.getItemAt(i);
            if (item.getLabel().equalsIgnoreCase(nombre)) {
                cboCategoria.setSelectedIndex(i);
                return;
            }
        }
    }

    private void onGuardar() {
        String nombre = txtNombre.getText().trim();
        String anioStr = txtAnio.getText().trim();
        ComboItem autorItem = (ComboItem) cboAutor.getSelectedItem();
        ComboItem categoriaItem = (ComboItem) cboCategoria.getSelectedItem();
        int estado = (cboEstado.getSelectedIndex() == 0) ? 1 : 0;

        // Validaciones
        String error = ValidationUtil.validateRequired(nombre, "Nombre");
        if (error != null) {
            ValidationUtil.showError(error);
            txtNombre.requestFocus();
            return;
        }
        
        error = ValidationUtil.validateYear(anioStr);
        if (error != null) {
            ValidationUtil.showError(error);
            txtAnio.requestFocus();
            return;
        }
        
        if (autorItem == null) {
            ValidationUtil.showError("Seleccione un autor");
            return;
        }
        
        if (categoriaItem == null) {
            ValidationUtil.showError("Seleccione una categoría");
            return;
        }

        int anio = Integer.parseInt(anioStr.trim());

        try {
            Libro l = new Libro(nombre, anio, autorItem.getId(), categoriaItem.getId(), estado);
            libroDAO.insertar(l);
            ValidationUtil.showSuccess("Libro guardado exitosamente");
            limpiarFormulario();
            cargarTabla();
        } catch (Exception ex) {
            ValidationUtil.showError("Error al guardar el libro: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void onActualizar() {
        if (selectedId == null) {
            ValidationUtil.showError("Seleccione un libro para actualizar");
            return;
        }

        String nombre = txtNombre.getText().trim();
        String anioStr = txtAnio.getText().trim();
        ComboItem autorItem = (ComboItem) cboAutor.getSelectedItem();
        ComboItem categoriaItem = (ComboItem) cboCategoria.getSelectedItem();
        int estado = (cboEstado.getSelectedIndex() == 0) ? 1 : 0;

        // Validaciones
        String error = ValidationUtil.validateRequired(nombre, "Nombre");
        if (error != null) {
            ValidationUtil.showError(error);
            txtNombre.requestFocus();
            return;
        }
        
        error = ValidationUtil.validateYear(anioStr);
        if (error != null) {
            ValidationUtil.showError(error);
            txtAnio.requestFocus();
            return;
        }
        
        if (autorItem == null) {
            ValidationUtil.showError("Seleccione un autor");
            return;
        }
        
        if (categoriaItem == null) {
            ValidationUtil.showError("Seleccione una categoría");
            return;
        }

        if (!ValidationUtil.showConfirmation("¿Está seguro de actualizar este libro?")) {
            return;
        }

        int anio = Integer.parseInt(anioStr.trim());

        try {
            Libro l = new Libro(selectedId, nombre, anio, autorItem.getId(), categoriaItem.getId(), estado);
            boolean ok = libroDAO.actualizar(l);
            if (ok) {
                ValidationUtil.showSuccess("Libro actualizado exitosamente");
                cargarTabla();
                seleccionarFilaPorId(selectedId);
            } else {
                ValidationUtil.showError("No se pudo actualizar el libro");
            }
        } catch (Exception ex) {
            ValidationUtil.showError("Error al actualizar el libro: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void onEliminar() {
        if (selectedId == null) {
            ValidationUtil.showError("Seleccione un libro para eliminar");
            return;
        }

        if (!ValidationUtil.showConfirmation("¿Está seguro de eliminar este libro?\nEsta acción marcará el libro como inactivo.")) {
            return;
        }

        try {
            boolean ok = libroDAO.eliminarLogico(selectedId);
            if (ok) {
                ValidationUtil.showSuccess("Libro eliminado exitosamente");
                limpiarFormulario();
                cargarTabla();
            } else {
                ValidationUtil.showError("No se pudo eliminar el libro");
            }
        } catch (Exception ex) {
            ValidationUtil.showError("Error al eliminar el libro: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void cargarTabla() {
        try {
            List<LibroConAutor> lista = libroDAO.listarConAutorYCategoria();
            model.setRowCount(0);
            for (LibroConAutor l : lista) {
                model.addRow(new Object[] {
                        l.getId(),
                        l.getNombre(),
                        l.getAnio(),
                        l.getAutorNombre(),
                        l.getCategoriaNombre(),
                        l.getEstado() == 1 ? "Activo" : "Inactivo"
                });
            }
        } catch (Exception ex) {
            ValidationUtil.showError("Error al cargar libros: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void aplicarFiltros() {
        String filtroNombre = txtFiltroNombre.getText().trim();
        ComboItem filtroCategoria = (ComboItem) cboFiltroCategoria.getSelectedItem();
        
        try {
            List<LibroConAutor> lista;
            
            if (filtroCategoria != null && filtroCategoria.getId() != -1 && !filtroNombre.isEmpty()) {
                // Filtrar por categoría y nombre
                lista = libroDAO.filtrarPorCategoriaYNombre(filtroCategoria.getId(), filtroNombre);
            } else if (filtroCategoria != null && filtroCategoria.getId() != -1) {
                // Solo filtrar por categoría
                lista = libroDAO.filtrarPorCategoria(filtroCategoria.getId());
            } else if (!filtroNombre.isEmpty()) {
                // Solo filtrar por nombre
                lista = libroDAO.filtrarPorNombre(filtroNombre);
            } else {
                // Sin filtros, mostrar todos
                cargarTabla();
                return;
            }
            
            model.setRowCount(0);
            for (LibroConAutor l : lista) {
                model.addRow(new Object[] {
                        l.getId(),
                        l.getNombre(),
                        l.getAnio(),
                        l.getAutorNombre(),
                        l.getCategoriaNombre(),
                        l.getEstado() == 1 ? "Activo" : "Inactivo"
                });
            }
        } catch (Exception ex) {
            ValidationUtil.showError("Error al aplicar filtros: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void limpiarFormulario() {
        txtNombre.setText("");
        txtAnio.setText("");
        if (cboAutor.getItemCount() > 0) cboAutor.setSelectedIndex(0);
        if (cboCategoria.getItemCount() > 0) cboCategoria.setSelectedIndex(0);
        cboEstado.setSelectedIndex(0);
        tblLibros.clearSelection();
        selectedId = null;
    }

    private void seleccionarFilaPorId(Integer id) {
        if (id == null) return;
        for (int i = 0; i < model.getRowCount(); i++) {
            Object val = model.getValueAt(i, 0);
            if (val != null && Integer.parseInt(val.toString()) == id) {
                tblLibros.setRowSelectionInterval(i, i);
                break;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Gestión de Libros");
            f.setContentPane(new LibroForm().panelPrincipal);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.pack();
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}