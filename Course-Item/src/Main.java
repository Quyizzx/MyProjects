
import javax.swing.*;
import java.util.List;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        initHospital();
        initPanel();
        initInfected();
    }
    /**
     * 初始化画布
     */
    private static void initPanel() {
        MyPanel p = new MyPanel();
        Thread panelThread = new Thread(p);
        JFrame frame = new JFrame();
        frame.add(p);
        frame.setSize(Constants.CITY_WIDTH + hospitalWidth + 300, Constants.CITY_HEIGHT);//设置画布总大小
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setTitle("瘟疫病毒模拟");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panelThread.start();
    }
    private static int hospitalWidth;

    /**
     * 初始化医院参数
     */
    private static void initHospital() {
        hospitalWidth = Hospital.getInstance().getWidth();
    }

    /**
     * 初始化初始感染者
     */
    private static void initInfected() {
        List<Person> people = PersonPool.getInstance().getPersonList();//获取所有的市民
        for (int i = 0; i < Constants.ORIGINAL_COUNT; i++) {
            Person person;
            do {
                person = people.get(new Random().nextInt(people.size() - 1));//随机挑选一个市民
            } while (person.isInfected());
            person.beInfected();//重新挑选感染者
        }
    }
}
