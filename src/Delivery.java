import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import static java.lang.Thread.sleep;

public class Delivery {
    private final BlockingQueue<Order> orderQueue = new ArrayBlockingQueue<>(10);
    private final Thread car1 = new Thread(new DeliveryCar());
    private final Thread car2 = new Thread(new DeliveryCar());
    private final Thread car3 = new Thread(new DeliveryCar());

    public Delivery() {
        car1.start();
        car2.start();
        car3.start();
    }

    private class DeliveryCar implements Runnable {
        private final int carID;
        private static int carCounter = 1;

        public DeliveryCar() {
            carID = carCounter++;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Order order = orderQueue.take(); // Throws: InterruptedException – if interrupted while waiting
                    int orderID = order.orderID;
                    System.out.println("The car#" + carID + " took the order #" + orderID);
                    sleep(500); //Throws: InterruptedException – if any thread has interrupted the current thread.
                    System.out.println("The car#" + carID + " delivered the order #" + orderID);
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

    public void deliveryStarter(int timeOfWork) throws InterruptedException {
        Thread queueFiller = new Thread(() -> {
            while (true) {
                try {
                    orderQueue.put(new Order()); // Throws: InterruptedException – if interrupted while waiting
                    sleep(1000); //Throws: InterruptedException – if any thread has interrupted the current thread.
                } catch (InterruptedException ie) {
                    car1.interrupt();
                    car2.interrupt();
                    car3.interrupt();
                    break;
                }
            }
        });

        queueFiller.start();
        sleep(timeOfWork);
        queueFiller.interrupt();
    }

    public static void main(String[] args) throws InterruptedException {
        new Delivery().deliveryStarter(10000);
    }
}
