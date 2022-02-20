import java.time.LocalTime;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

public class Delivery {
    //    BlockingQueue<Order> orderQueue = new LinkedBlockingQueue<>();
    List<Order> orderList = new LinkedList<>();
    BlockingQueue<Order> orderQueue = new ArrayBlockingQueue<>(100);

    public Delivery() {
        new Thread(new DeliveryCar()).start();
        new Thread(new DeliveryCar()).start();
        new Thread(new DeliveryCar()).start();
    }

    private class DeliveryCar implements Runnable {
        private final int carID;
        private static int carCounter = 0;

        public DeliveryCar() {
            carID = carCounter++;
        }

        @Override
        public void run() {
            try {
                while (true) {
//                    Iterator<Order> orderIterator = orderQueue.iterator();
//                    LocalTime earlyDeliveryTime = LocalTime.now();
                    int earlyDeliveryIndex = 0;
//                    while (orderIterator.hasNext()) {
//                        LocalTime currentDeliveryTime = orderIterator.next().getFinalDeliveryTime();
//                        if (currentDeliveryTime.isBefore(earlyDeliveryTime)) earlyDeliveryTime = currentDeliveryTime;
//                    }
                    for (int i = 0; i < orderQueue.size(); i++) {
                        LocalTime currentDeliveryTime = orderQueue.take().getFinalDeliveryTime();
                    }

                    Order order = orderQueue.poll(500, TimeUnit.MILLISECONDS);
                    if (order != null) {
                        String orderID = order.orderID;
                        System.out.println("The car " + carID + "took the order #" + orderID);
                        int deliveryTime = 500 + (int) (Math.random() * 500);
                        sleep(deliveryTime);
                        System.out.println("The car " + carID + "delivered the order #" + orderID);
                    }
                }
            } catch (InterruptedException e) { // if interrupted while waiting
                e.printStackTrace();
            }
        }
    }

    private static class Order {
        private final String orderID;
        private final LocalTime finalDeliveryTime;

        public Order(int timeForDeliveryInSeconds) {
            orderID = UUID.randomUUID().toString();
            finalDeliveryTime = LocalTime.now().plusSeconds(timeForDeliveryInSeconds);
        }

        public LocalTime getFinalDeliveryTime() {
            return finalDeliveryTime;
        }
    }

    public void deliveryStarter(int timeOfWork) throws InterruptedException {
        Thread delivery = new Thread(() -> {
            while (true) {
                try {
                    Order o = new Order(1 + (int) (Math.random() * 2));
                    orderQueue.add(o);
                    orderList.add(o);
                    sleep(400);
                } catch (InterruptedException e) {
//                    e.printStackTrace();
                    break;
                }
            }
        });

        delivery.start();
        sleep(timeOfWork);
        delivery.interrupt();
    }

    public static void main(String[] args) throws InterruptedException {
        new Delivery().deliveryStarter(10000);
    }
}
