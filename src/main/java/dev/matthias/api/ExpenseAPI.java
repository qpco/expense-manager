package dev.matthias.api;

import com.google.gson.Gson;
import dev.matthias.data.ExpenseDAO;
import dev.matthias.data.ExpenseDAOPostgres;
import dev.matthias.entities.Expense;
import dev.matthias.service.ExpenseService;
import dev.matthias.service.ExpenseServiceImpl;
import dev.matthias.exceptions.EmployeeNotFoundException;
import dev.matthias.utilities.LogLevel;
import dev.matthias.utilities.Logger;
import dev.matthias.utilities.ParseStatus;
import dev.matthias.utilities.Status;
import io.javalin.http.Handler;

import java.util.List;
import java.util.Locale;

public class ExpenseAPI {

    static Gson gson = new Gson();
    static ExpenseDAO expenseDAO = new ExpenseDAOPostgres();
    static ExpenseService expenseService = new ExpenseServiceImpl();

    private ExpenseAPI(){}

    static Handler createExpense() {
        return context -> {
            String body = context.body();
            try {
                Expense newExpense = expenseService.createExpense(
                        gson.fromJson(body, Expense.class));
                context.status(201);
                context.result("Created expense: " + newExpense.getId());
            } catch (EmployeeNotFoundException e) {
                context.status(404);
                context.result("Employee not found.");
            }
        };
    }

    static Handler readExpenses() {
        return context -> {
            String param = context.queryParam("status");
            StringBuilder display = new StringBuilder();
            List<Expense> expenses = expenseDAO.readAllExpenses();
            if(expenses.isEmpty()) {
                context.status(404);
                context.result("No expenses found.");
            }
            if(param != null) {
                Status status = ParseStatus.getStatus(param.toUpperCase(Locale.ROOT));
                display = readExpenseStatus(status, expenses);
                context.status(200);
            } else {
                context.status(200);
                for(Expense e : expenses) display.append(e.toString()).append("\n");
            }
            context.result(String.valueOf(display));
        };
    }

    static StringBuilder readExpenseStatus(Status status, List<Expense> expenses) {
        StringBuilder display = new StringBuilder();
        expenses.stream().filter(e -> e.getStatus().equals(status))
                .forEach(e -> display.append(e).append("\n"));
        return display;
    }

    static Handler readExpense() {
        return context -> {
            int id = Integer.parseInt(context.pathParam("id"));
            Expense expense = expenseService.readExpense(id);
            if(expense == null) {
                context.status(404);
                context.result("Expense not found.");
            } else {
                context.status(200);
                context.result(expense.toString());
            }
        };
    }

    static Handler updateExpense() {
        return context -> {
            int id = Integer.parseInt(context.pathParam("id"));
            String body = context.body();
            Expense expense = gson.fromJson(body, Expense.class);
            expense.setId(id);
            expense = expenseService.updateExpense(expense);
            if(expense == null) {
                context.status(404);
            } else {
                context.status(200);
                context.result("Updated expense: " + expense.getId());
            }
        };
    }

    static Handler approveOrDenyExpense() {
        return context -> {
            int id = Integer.parseInt(context.pathParam("id"));
            String status = context.pathParam("status");
            Expense expense = expenseService.readExpense(id);
            Status s = ParseStatus.getStatus(status);
            if(expense == null) {
                context.status(404);
                context.result("Expense not found.");
                return;
            } else if(!expense.getStatus().equals(Status.PENDING)) {
                context.result("Expense already approved or denied.");
                context.status(400);
                return;
            }

            switch(s) {
                case PENDING: context.result("Expense already pending."); break;
                case APPROVED:
                    expense.setStatus(Status.APPROVED);
                    expenseService.updateExpense(expense);
                    context.status(200);
                    Logger.log("Approved Expense: " + expense.getId(), LogLevel.INFO);
                    context.result("Expense: " + expense.getId() + " approved.");
                    break;
                case DENIED:
                    expense.setStatus(Status.DENIED);
                    expenseService.updateExpense(expense);
                    context.status(200);
                    Logger.log("Denied Expense: " + expense.getId(), LogLevel.INFO);
                    context.result("Expense: " + expense.getId() + " denied.");
                    break;
                default: context.result("Invalid status."); break;
            }
        };
    }

    static Handler deleteExpense() {
        return context -> {
            int id = Integer.parseInt(context.pathParam("id"));
            if(expenseService.deleteExpense(id)) {
                context.status(200);
                context.result("Deleted expense: " + id);
            } else {
                context.status(404);
                context.result("Expense: " + id + " not found.");
            }
        };
    }
}
