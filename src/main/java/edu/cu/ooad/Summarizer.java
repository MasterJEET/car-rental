package edu.cu.ooad;

import edu.cu.ooad.util.*;

import java.util.List;
import java.util.Objects;

public class Summarizer implements Observer {
    private Observable observable;

    public Summarizer(Observable observable) {
        this.observable = observable;
        observable.registerObserver(this);
    }

    @Override
    public void update(Object object) {
        if(!Objects.equals(
                object.getClass().getSimpleName(),
                Recorder.class.getSimpleName()))
        {
            System.err.println("Expected a Recorder object, got " + object.getClass().getSimpleName());
        }
        else {
            Recorder recorder = (Recorder)object;
            switch (recorder.getAction()) {
                case GENERATE_DAILY_REPORT:
                {
                    generateDailyReport(recorder);
                    break;
                }
                case GENERATE_OVERALL_STATUS:
                {
                    generateOverallStatus(recorder);
                    break;
                }
                default:
                {
                    System.err.println(
                            "Not creating report, unknown action specified: " +
                                    recorder.getAction().toString()
                    );
                    break;
                }
            }
        }
    }

    private void generateDailyReport(Recorder recorder) {
        Report report = new Report();
        report.type = Report.Type.DAILY_REPORT;
        report.dayNumber = recorder.getDayNumber();

        List<Transaction> completedRentals = recorder.getTransactionsOfStatus(RentalStatus.COMPLETE);
        if (completedRentals != null) {
            for (Transaction transaction :completedRentals) {
                report.completedRentals.add(new Transaction(transaction));
            }
        }

        List<Transaction> activeRentals = recorder.getTransactionsOfStatus(RentalStatus.ACTIVE);
        if (activeRentals != null) {
            for (Transaction transaction :activeRentals) {
                report.activeRentals.add(new Transaction(transaction));
            }
        }

        report.availableCars = recorder.getAvailableCars();

        List<Transaction> transactions = recorder.getTransactionsOfDay();
        if (transactions != null) {
            for (Transaction transaction : transactions) {
                report.transactions.add(new Transaction(transaction));
            }
        }

        recorder.addReportForDay(report.dayNumber, report);
    }

    private void generateOverallStatus(Recorder recorder) {
        Report report = new Report();
        report.type = Report.Type.OVERALL_STATUS;
        report.dayNumber = recorder.getDayNumber();
        report.completedRentals = recorder.getTransactionsOfStatus(RentalStatus.COMPLETE);
        report.activeRentals = recorder.getTransactionsOfStatus(RentalStatus.ACTIVE);

        recorder.setOverallStatusReport(report);
    }

    public void close() {
        observable.removeObserver(this);
    }

}
