package edu.cu.ooad;

import edu.cu.ooad.util.Observable;
import edu.cu.ooad.util.Observer;

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
            //TODO: Generate summary using data from Recorder
            Recorder recorder = (Recorder)object;
            switch (recorder.getAction()) {
                case GENERATE_DAILY_REPORT:
                {
                    //TODO: Generate and store daily report
                    System.out.println("Daily report needs to be created.");
                    break;
                }
                case GENERATE_FINAL_REPORT:
                {
                    //TODO: Generate and store overall final report
                    System.out.println("Final report needs to be created.");
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

    public void close() {
        observable.removeObserver(this);
    }
}
