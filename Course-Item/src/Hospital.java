
import java.util.ArrayList;
import java.util.List;


public class Hospital extends Point {
    /**
     * 医院类
     * 包含 1.医院床位的设定，被占用或空闲
     */
    public static final int HOSPITAL_X = 720;
    public static final int HOSPITAL_Y = 80;
    private int width;
    private int height = 600;

    public int getWidth() {
        return width;
    }


    public int getHeight() {
        return height;
    }


    private static Hospital hospital = new Hospital();

    public static Hospital getInstance() {
        return hospital;
    }

    private Point point = new Point(HOSPITAL_X, HOSPITAL_Y);//第一个床位所在坐标，用于给其他床位定绝对坐标
    private List<Bed> beds = new ArrayList<>();

    public List<Bed> getBeds() {
        return beds;
    }

    private Hospital() {
        //医院矩形所在坐标
        super(HOSPITAL_X, HOSPITAL_Y + 10);
        //根据床位数量调整医院矩形的大小
        if (Constants.BED_COUNT == 0) {
            width = 0;
            height = 0;
        }
        //根据医院床位数量计算医院宽度
        //因为高度定了只能装载100个床位
        int column = Constants.BED_COUNT / 100;
        width = column * 6;
        //根据第一个床位坐标初始化其他床位的坐标
        for (int i = 0; i < column; i++) {

            for (int j = 10; j <= 606; j += 6) {

                Bed bed = new Bed(point.getX() + i * 6, point.getY() + j);
                beds.add(bed);
                if (beds.size() >= Constants.BED_COUNT) {//确定医院床位承载数量1000
                    break;
                }
            }

        }
    }

    /**
     * 使用床位
     */
    public Bed pickBed() {
        for (Bed bed : beds) {
            if (bed.isEmpty()) {
                return bed;
            }
        }
        return null;
    }

    /**
     * 死亡或痊愈出院空出床位
     */
    public Bed returnBed(Bed bed) {
        if (bed != null) {
            bed.setEmpty(true);
        }
        return bed;
    }
}
