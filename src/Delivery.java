import java.util.concurrent.*;
import static java.lang.Thread.sleep;

public class Delivery {
    private final BlockingQueue<Order> orderQueue = new ArrayBlockingQueue<>(10);
    private final int car1DeliveryTime = 500;
    private final int car2DeliveryTime = 200;
    private final int car3DeliveryTime = 700;
    DeliveryCar car1 = new DeliveryCar(car1DeliveryTime);
    DeliveryCar car2 = new DeliveryCar(car2DeliveryTime);
    DeliveryCar car3 = new DeliveryCar(car3DeliveryTime);
    private final Thread threadCar1 = new Thread(car1);
    private final Thread threadCar2 = new Thread(car2);
    private final Thread threadCar3 = new Thread(car3);

    public Delivery() {
        threadCar1.start();
        threadCar2.start();
        threadCar3.start();
    }

    public void queueFiller(int timeOfWork, int breakTime) throws InterruptedException {
        Thread filler = new Thread(() -> {
            while (true) {
                try {
                    orderQueue.put(new Order()); // Throws: InterruptedException – if interrupted while waiting
                    sleep(breakTime); //Throws: InterruptedException – if any thread has interrupted the current thread.
                } catch (InterruptedException ie) {
                    try {
                        sleep(getMax(car1DeliveryTime, car2DeliveryTime, car3DeliveryTime));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    threadCar1.interrupt();
                    threadCar2.interrupt();
                    threadCar3.interrupt();
                    break;
                }
            }
        });

        filler.start();
        sleep(timeOfWork);
        filler.interrupt();
        threadCar1.join();
        threadCar2.join();
        threadCar3.join();
    }

    public void resultPrinter() {
        System.out.println("The Car1 delivered " + car1.getDeliveryCounter() + " orders");
        System.out.println("The Car2 delivered " + car2.getDeliveryCounter() + " orders");
        System.out.println("The Car3 delivered " + car3.getDeliveryCounter() + " orders");
    }

    private int getMax(int a, int b, int c) {
        return a > b ? (Math.max(a, c)) : (Math.max(b, c));
    }

    private class DeliveryCar implements Runnable {
        private final int carID;
        private static int carCounter = 1;
        private final int deliveryTime;
        private int deliveryCounter = 0;

        public DeliveryCar(int deliveryTime) {
            carID = carCounter++;
            this.deliveryTime = deliveryTime;
        }

        public int getDeliveryCounter() {
            return deliveryCounter;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Order order = orderQueue.take(); // Throws: InterruptedException – if interrupted while waiting
                    int orderID = order.orderID;
                    System.out.println("The car #" + carID + " took the order #" + orderID);
                    sleep(deliveryTime); //Throws: InterruptedException – if any thread has interrupted the current thread.
                    System.out.println("The car #" + carID + " delivered the order #" + orderID);
                    deliveryCounter++;
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

    public static void main(String[] args) throws InterruptedException {
        Delivery delivery = new Delivery();
        delivery.queueFiller(5000, 200);
        delivery.resultPrinter();
    }
}
