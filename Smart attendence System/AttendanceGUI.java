import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;


class Student {
    int id;
    String name;
    int present;
    int total;

    public Student(int id, String name) {
        this.id = id;
        this.name = name;
        this.present = 0;
        this.total = 0;
    }

    public double getPercentage() {
        if (total == 0) return 0;
        return (present * 100.0) / total;
    }
}


class AttendanceSystem {

    ArrayList<Student> students = new ArrayList<>();

    public boolean addStudent(int id, String name) {
        if (searchStudent(id) != null) return false;
        students.add(new Student(id, name));
        return true;
    }

    public Student searchStudent(int id) {
        for (Student s : students) {
            if (s.id == id) return s;
        }
        return null;
    }

    public boolean markAttendance(int id, boolean present) {
        Student s = searchStudent(id);
        if (s != null) {
            if (present) s.present++;
            s.total++;
            return true;
        }
        return false;
    }

    public void updateStudent(int id, String newName) {
        Student s = searchStudent(id);
        if (s != null) {
            s.name = newName;
        }
    }

    public void sortById(boolean ascending) {
        if (ascending) {
            students.sort((a, b) -> a.id - b.id);
        } else {
            students.sort((a, b) -> b.id - a.id);
        }
    }
}


public class AttendanceGUI extends JFrame {

    AttendanceSystem system = new AttendanceSystem();

    JTextField idField, nameField, searchField;
    JTable table;
    DefaultTableModel model;

    boolean ascending = true;

    public AttendanceGUI() {

        setTitle("Smart Student Attendance System");
        setSize(900, 550);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

       
        getContentPane().setBackground(new Color(40, 40, 40));

      
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBackground(new Color(50, 50, 50));

        idField = new JTextField(6);
        nameField = new JTextField(10);
        searchField = new JTextField(6);

        Font font = new Font("Raleway", Font.PLAIN, 14);

        JButton addBtn = new JButton("Add");
        JButton presentBtn = new JButton("Present");
        JButton absentBtn = new JButton("Absent");
        JButton searchBtn = new JButton("Search");
        JButton sortBtn = new JButton("Sort ↑");
        JButton editBtn = new JButton("Edit");

        JButton[] buttons = {addBtn, presentBtn, absentBtn, searchBtn, sortBtn, editBtn};

        for (JButton b : buttons) {
            b.setBackground(new Color(70, 70, 70));
            b.setForeground(Color.WHITE);
            b.setFocusPainted(false);
            b.setFont(font);
        }

        JLabel l1 = new JLabel("ID");
        JLabel l2 = new JLabel("Name");
        JLabel l3 = new JLabel("Search ID");

        JLabel[] labels = {l1, l2, l3};
        for (JLabel l : labels) {
            l.setForeground(Color.CYAN);
            l.setFont(font);
        }

        panel.add(l1);
        panel.add(idField);

        panel.add(l2);
        panel.add(nameField);

        panel.add(addBtn);
        panel.add(editBtn);

        panel.add(presentBtn);
        panel.add(absentBtn);

        panel.add(l3);
        panel.add(searchField);
        panel.add(searchBtn);

        panel.add(sortBtn);

        add(panel, BorderLayout.NORTH);

        
        model = new DefaultTableModel();

        model.addColumn("ID");
        model.addColumn("Name");
        model.addColumn("Present");
        model.addColumn("Total");
        model.addColumn("Attendance %");

        table = new JTable(model);

        table.setBackground(new Color(45, 45, 45));
        table.setForeground(Color.white);
        table.setRowHeight(25);
        table.setFont(font);

        table.getTableHeader().setBackground(new Color(60, 60, 60));
        table.getTableHeader().setForeground(Color.white);
        table.getTableHeader().setFont(new Font("Raleway", Font.BOLD, 14));

        add(new JScrollPane(table), BorderLayout.CENTER);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {

                int row = table.getSelectedRow();

                if (row != -1) {

                    int id = Integer.parseInt(
                            table.getValueAt(row, 0).toString()
                    );

                    system.markAttendance(id, true);
                    refreshTable();
                }
            }
        });

       

        addBtn.addActionListener(e -> {
            int id = Integer.parseInt(idField.getText());
            String name = nameField.getText();

            boolean added = system.addStudent(id, name);

            if (!added) {
                JOptionPane.showMessageDialog(this, "Duplicate ID not allowed!");
            }

            refreshTable();
        });

        editBtn.addActionListener(e -> {
            int id = Integer.parseInt(idField.getText());
            String name = nameField.getText();

            Student s = system.searchStudent(id);

            if (s != null) {
                system.updateStudent(id, name);
                refreshTable();
                JOptionPane.showMessageDialog(this, "Student Updated");
            } else {
                JOptionPane.showMessageDialog(this, "Student Not Found");
            }
        });

        presentBtn.addActionListener(e -> {
            system.markAttendance(Integer.parseInt(idField.getText()), true);
            refreshTable();
        });

        absentBtn.addActionListener(e -> {
            system.markAttendance(Integer.parseInt(idField.getText()), false);
            refreshTable();
        });

        searchBtn.addActionListener(e -> {
            int id = Integer.parseInt(searchField.getText());

            for (int i = 0; i < system.students.size(); i++) {
                Student s = system.students.get(i);

                if (s.id == id) {
                    table.setRowSelectionInterval(i, i);

                    JOptionPane.showMessageDialog(this,
                            "FOUND\nID: " + s.id +
                                    "\nName: " + s.name +
                                    "\nAttendance: " +
                                    String.format("%.2f", s.getPercentage()) + "%");
                    return;
                }
            }

            JOptionPane.showMessageDialog(this, "Student Not Found");
        });

        sortBtn.addActionListener(e -> {
            system.sortById(ascending);
            refreshTable();

            if (ascending) {
                sortBtn.setText("Sort ↓");
            } else {
                sortBtn.setText("Sort ↑");
            }

            ascending = !ascending;
        });

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    
    public void refreshTable() {
        model.setRowCount(0);

        for (Student s : system.students) {
            model.addRow(new Object[]{
                    s.id,
                    s.name,
                    s.present,
                    s.total,
                    String.format("%.2f", s.getPercentage())
            });
        }
    }


    public static void main(String[] args) {
        new AttendanceGUI();
    }
}