package dev.matthias.data;

import dev.matthias.entities.Expense;
import dev.matthias.exceptions.ExpenseNotFoundException;
import dev.matthias.utilities.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExpenseDAOPostgres implements ExpenseDAO{

    @Override
    public Expense createExpense(Expense expense) {
        try {
            String query = "insert into expense values (?, ?, default, ?, ?)";
            Connection conn = ConnectionUtil.createConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, expense.getId());
            ps.setString(2, expense.getName());
            ps.setDouble(3, expense.getCost());
            ps.setInt(4, expense.getIssuerId());
            if(ps.executeUpdate() == 1) {
                Logger.log("Created expense: " + expense.getId(), LogLevel.INFO);
                return expense;
            } else {
                Logger.log("Failed to create expense: " + expense.getId(), LogLevel.WARNING);
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Expense readExpense(int id) throws ExpenseNotFoundException {
        try {
            String query = "select * from expense where expense_id = ?";
            Connection conn = ConnectionUtil.createConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                Status s = ParseStatus.getStatus(rs.getString(3));
                return new Expense(rs.getInt(1), rs.getString(2), s, rs.getDouble(4), rs.getInt(5));
            } else throw new ExpenseNotFoundException();

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Expense updateExpense(Expense expense) {
        try {
            String query = "update expense set name = ?, status = ?, cost = ? where expense_id = ?";
            Connection conn = ConnectionUtil.createConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, expense.getName());
            ps.setString(2, expense.getStatus().toString());
            ps.setDouble(3, expense.getCost());
            ps.setInt(4, expense.getId());

            if(ps.executeUpdate() == 1) {
                Logger.log("Updated expense: " + expense.getId(), LogLevel.INFO);
                return expense;
            } else return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean deleteExpense(int id) throws ExpenseNotFoundException {
        try {
            String query = "delete from expense where expense_id = ?";
            Connection conn = ConnectionUtil.createConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);

            if(ps.executeUpdate() == 1) {
                Logger.log("Deleted employee: " + id, LogLevel.INFO);
                return true;
            } else throw new ExpenseNotFoundException();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Expense> readAllExpenses() {
        List<Expense> expenseList = new ArrayList<>();
        try {
            String query = "select * from expense";
            Connection conn = ConnectionUtil.createConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Expense e = new Expense();
                e.setId(rs.getInt(1));
                e.setName(rs.getString(2));
                Status s = ParseStatus.getStatus(rs.getString(3));
                e.setStatus(s);
                e.setCost(rs.getDouble(4));
                e.setIssuerId(rs.getInt(5));
                expenseList.add(e);
            }
            return expenseList;

        } catch (SQLException e) {
            e.printStackTrace();
            return expenseList;
        }
    }
}
