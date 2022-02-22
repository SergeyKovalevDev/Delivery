import java.time.LocalTime;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Test {
    private final BlockingQueue<Order> queue = new ArrayBlockingQueue(5);

    public static void main(String[] args) throws InterruptedException {
        Test t = new Test();
        t.queueFiller();
        t.queuePrinter();
//        t.iteration();
//        t.queuePrinter();

    }

    private static class Order {
        private final int orderID;
        private final LocalTime finalDeliveryTime;

        public Order(int orderID, int timeForDeliveryInSeconds) {
            this.orderID = orderID;
            finalDeliveryTime = LocalTime.now().plusSeconds(timeForDeliveryInSeconds);
        }

        @Override
        public String toString() {
            return "Order " + orderID;
        }
    }

    private void queueFiller() throws InterruptedException {
        queue.put(new Order(1, 4));
        queue.put(new Order(2, 5));
        queue.put(new Order(3, 6));
        queue.put(new Order(4, 7));
        queue.put(new Order(5, 5));
    }

    private void queuePrinter() throws InterruptedException {
        System.out.println(queue.take());
        System.out.println(queue.take());
        System.out.println(queue.take());
        System.out.println(queue.take());
        System.out.println(queue.take());
    }

    private void iteration() {
        Iterator<Order> orderIterator = queue.iterator();
        while (orderIterator.hasNext()) {
            System.out.println(orderIterator.next());
//            orderIterator.next().
        }
    }
}
