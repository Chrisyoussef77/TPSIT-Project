class MySemaphore {
    private int permits;
    public MySemaphore(int permits) // permits = 4 (3adad el tarabezat)
{
        this.permits = permits;
    }
    public synchronized void acquire() // el thread 7agz permit
    { 
        while (permits == 0) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        permits--; // lw fy permit mota7 bn2llo b wa7d 3l4an el thread 7agz makan
    }
    public synchronized void release() {
        permits++; // bnrg3 el makan w nzwd 3adad el amakn el mota7a
        notifyAll();
    }
}
class Family extends Thread {
    int id;
    public Family(int id) {
        this.id = id;
    }
    public void run() {
        for (int attempt = 1; attempt <= 5; attempt++) {       
            Main.stations.acquire(); // el family bt7awl t7gz tarabeza
            Main.mutex.acquire();// bn7gz el mutex 2abl ai ta3dilat
            Main.busy++; // hanzwd 3adad el tarabezat el most5dma b wa7d 
            if (Main.busy > Main.maxBusy) // bakarn el 3adad el 7ali b akbar 3adad wsltlo 2abl keda
             {
                Main.maxBusy = Main.busy;
            }
            Main.mutex.release();
            Main.mutex.acquire();
            if (Main.stock > 0) {
                Main.stock--; // bn2s box 3l4an edeto le family da fy 7alt en fy boxes mota7a asln w el condition sha8ala
                Main.served++; // bazwd 3add el 3a2lat eli 5adt box
            } else {
                Main.rejected++; // da fy 7alt en mafi4 boxes mota7a asln fa el condition m4 hat4t8l
            }
            Main.mutex.release();
            try {
                Thread.sleep(10); // faradt en el 3ela bt3od 10 ms 3la el tarabeza fa haw2f el thread nafs el moda (10ms)
            } catch (InterruptedException e) {
            }
            Main.mutex.acquire();
            Main.busy--; // b2ll 3add el tarabezat el most5dma b wa7d 
            Main.mutex.release();
            Main.stations.release(); // k2ni b7rr el tarabeza aw b3ln en fy tarabeza fadia w hena el family btb2a 5alst w btrg3 el permit 
        }
    }
}
public class Main {
    static int stock = 50;
    static int served = 0;
    static int rejected = 0;
    static int busy = 0;
    static int maxBusy = 0;
    static MySemaphore mutex = new MySemaphore(1);
    static MySemaphore stations = new MySemaphore(4);
    public static void main(String[] args) throws InterruptedException {
        Family[] families = new Family[8]; // array mtkwn mn 8 3a2lat
        for (int i = 0; i < 8; i++) {
            families[i] = new Family(i + 1);
            families[i].start(); // ba48l el thread 
        }
        for (int i = 0; i < 8; i++) {
            families[i].join(); // hatstna kol 3ela t5ls 4o8lha
        }
        System.out.println("Boxes given: " + served);
        System.out.println("Visits without a box: " + rejected);
        System.out.println("Boxes left: " + stock);
        System.out.println("Most tables busy at the same time: " + maxBusy);
    }
}