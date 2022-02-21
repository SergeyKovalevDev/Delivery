import java.util.concurrent.*;
import static java.lang.Thread.sleep;

public class Delivery {
    private final BlockingQueue<Order> orderQueue = new ArrayBlockingQueue<>(10);
    private final Thread threadDeliveryCar1 = new Thread(new DeliveryCar());
    private final Thread threadDeliveryCar2 = new Thread(new DeliveryCar());
    private final Thread threadDeliveryCar3 = new Thread(new DeliveryCar());

    public Delivery() {
        threadDeliveryCar1.start();
        threadDeliveryCar2.start();
        threadDeliveryCar3.start();
    }

    private class DeliveryCar implements Runnable {
        private final int carID;
        private static int carCounter = 1;
        int orderID;
        private static final int DELIVERY_TIME = 500;

        public DeliveryCar() {
            carID = carCounter++;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Order order = orderQueue.take(); // Throws: InterruptedException – if interrupted while waiting
                    orderID = order.orderID;
                    System.out.println("The car #" + carID + " took the order #" + orderID);
                    sleep(DELIVERY_TIME); //Throws: InterruptedException – if any thread has interrupted the current thread.
                    System.out.println("The car #" + carID + " delivered the order #" + orderID);
                } catch (InterruptedException ie) {
                    break;
                }
            }
        }
    }

    private static class Order {
        private final int orderID;
        private static int orderCounter = 1;

        public Order() {
            orderID = orderCounter++;
        }
    }

    public void queueFiller(int timeOfWork) throws InterruptedException {
        Thread queueFiller = new Thread(() -> {
            while (true) {
                try {
                    orderQueue.put(new Order()); // Throws: InterruptedException – if interrupted while waiting
                    sleep(900); //Throws: InterruptedException – if any thread has interrupted the current thread.
                } catch (InterruptedException ie) {
                    try {
                        sleep(DeliveryCar.DELIVERY_TIME);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    threadDeliveryCar1.interrupt();
                    threadDeliveryCar2.interrupt();
                    threadDeliveryCar3.interrupt();
                    break;
                }
            }
        });

        queueFiller.start();
        sleep(timeOfWork);
        queueFiller.interrupt();
    }

    public static void main(String[] args) throws InterruptedException {
        new Delivery().queueFiller(10000);
    }
}
