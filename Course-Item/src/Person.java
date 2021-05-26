
import java.util.List;
import java.util.Random;
/**
 * 能够随机运动的民众
 * 包含 1.居民的多种状态及初始化，
 * 2.居民的运动状态和运动方向，
 * 3.不同居民之间感染的可能性，
 * 4.居民感染后需入院治疗。
 * 正态分布用于
 * 1.初始市民位置的随机生成
 * 2.产生的个人实例是否存在流动意愿
 * 3.根据流动意愿来决定感染者的流动方向
 * 4.依据从潜伏期到发病期为25天随机生成潜伏期的发病时间。
 */

public class Person extends Point {
    private City city;

    private MoveTarget moveTarget;
    /**
     * 人群流动意愿影响系数：正态分布方差sigma
     */
    int sig = 1;

    /**
     * 正态分布N(mu,sigma)随机位移目标位置
     */

    double targetXU;//x方向的均值mu
    double targetYU;//y方向的均值mu
    double targetSig = 50;//方差sigma

    /**
     * 市民的状态
     */
    public interface State {
        int NORMAL = 0;//正常人，未感染的健康人
        int SUSPECTED = NORMAL + 1;//有暴露感染风险
        int SHADOW = SUSPECTED + 1;//潜伏期
        int CONFIRMED = SHADOW + 1;//发病且已确诊为感染病人
        int FREEZE = CONFIRMED + 1;//隔离治疗，禁止位移
        int DEATH = FREEZE + 1;//病死者
    }


    public Person(City city, int x, int y) {
        super(x, y);
        this.city = city;
        //对市民的初始位置进行N(x,100)的正态分布随机
        targetXU = MathUtil.stdGaussian(100, x);
        targetYU = MathUtil.stdGaussian(100, y);

    }

    /**
     * 流动意愿标准化
     */
    public boolean wantMove() {
        return MathUtil.stdGaussian(sig, Constants.u) > 0;
    }

    private int state = State.NORMAL;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    int infectedTime = 0;//感染时刻
    int confirmedTime = 0;//确诊时刻
    int dieMoment = 0;//死亡时刻，为0代表未确定，-1代表不会病死


    public boolean isInfected() {
        return state >= State.SHADOW;
    }

    /**
     * 被感染
     */
    public void beInfected() {
        state = State.SHADOW;
        infectedTime = MyPanel.worldTime;
    }

    /**
     * 计算两点之间的直线距离
     */
    public double distance(Person person) {
        return Math.sqrt(Math.pow(getX() - person.getX(), 2) + Math.pow(getY() - person.getY(), 2));
    }

    /**
     * 住院
     */
    private void freezy() {
        state = State.FREEZE;
    }

    /**
     * 不同状态下的单个人实例运动行为
     * 三种方法判断运动状态
     */
    private void action() {

        if (state == State.FREEZE || state == State.DEATH) {
            return;//如果处于隔离或者死亡状态，则无法行动
        }

        if (!wantMove()) {
            return;
        }//随机生成的流动意愿小于0的，则无法行动

        //存在流动意愿的，将进行流动，流动位移仍然遵循标准正态分布
        if (moveTarget == null || moveTarget.isArrived()) {
            //在想要移动并且没有目标时，将自身移动目标设置为随机生成的符合正态分布的目标点
            //产生N(a,b)的数：Math.sqrt(b)*random.nextGaussian()+a
            double targetX = MathUtil.stdGaussian(targetSig, targetXU);
            double targetY = MathUtil.stdGaussian(targetSig, targetYU);
            moveTarget = new MoveTarget((int) targetX, (int) targetY);

        }

        //计算运动位移
        int dX = moveTarget.getX() - getX();
        int dY = moveTarget.getY() - getY();

        double length = Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2));//与目标点的距离

        if (length < 1) {
            //判断是否到达目标点
            moveTarget.setArrived(true);
            return;
        }

        int udX = (int) (dX / length);//x轴dX为位移量，符号为沿x轴前进方向, 即udX为X方向表示量
        if (udX == 0 && dX != 0) {
            if (dX > 0) {
                udX = 1;
            } else {
                udX = -1;
            }
        }


        int udY = (int) (dY / length);//y轴dY为位移量，符号为沿x轴前进方向，即udY为Y方向表示量
        if (udY == 0 && dY != 0) {
            if (dY > 0) {
                udY = 1;
            } else {
                udY = -1;
            }
        }

        //横向运动边界
        if (getX() > Constants.CITY_WIDTH || getX() < 0) {
            moveTarget = null;
            if (udX > 0) {
                udX = -udX;
            }
        }
        //纵向运动边界
        if (getY() > Constants.CITY_HEIGHT || getY() < 0) {
            moveTarget = null;
            if (udY > 0) {
                udY = -udY;
            }
        }
        moveTo(udX, udY);

    }

    public Bed useBed;

    private float SAFE_DIST = 2f;//安全距离

    /**
     * 对各种状态的人进行不同的处理，更新发布市民健康状态
     */
    public void update() {

        if (state == State.FREEZE || state == State.DEATH) {
            return;//如果已经隔离或者死亡了，就不需要处理了
        }

        //处理已经确诊的感染者死亡或者痊愈
        if (state == State.CONFIRMED && dieMoment == 0) {

            int destiny = new Random().nextInt(10000) + 1;//产生[1,10000]随机数
            if (1 <= destiny && destiny <= (int) (Constants.FATALITY_RATE * 10000)) {

                //如果数字落在死亡区间
                int dieTime = (int) MathUtil.stdGaussian(Constants.DIE_VARIANCE, Constants.DIE_TIME);
                dieMoment = confirmedTime + dieTime;//发病后确定死亡时刻
            } else {
                dieMoment = -1;//痊愈

            }
        }

        if (state == State.CONFIRMED
                && MyPanel.worldTime - confirmedTime >= Constants.HOSPITAL_RECEIVE_TIME) {
            //如果患者已经确诊，且（世界时刻-确诊时刻）大于医院响应时间，即医院准备好病床了，可以抬走了
            Bed bed = Hospital.getInstance().pickBed();//查找空床位
            if (bed == null) {
                //没有床位了，报告需求床位数

            } else {
                //安置病人
                useBed = bed;
                state = State.FREEZE;
                setX(bed.getX());
                setY(bed.getY());
                bed.setEmpty(false);
            }
        }

        //处理病死者
        if ((state == State.CONFIRMED || state == State.FREEZE) && MyPanel.worldTime >= dieMoment && dieMoment > 0) {
            state = State.DEATH;//患者死亡
            Hospital.getInstance().returnBed(useBed);//归还床位
        }

        //增加一个正态分布用于潜伏期内随机发病时间
        double stdRnShadowtime = MathUtil.stdGaussian(25, Constants.SHADOW_TIME / 2);
        //处理发病的潜伏期感染者
        if (MyPanel.worldTime - infectedTime > stdRnShadowtime && state == State.SHADOW) {
            state = State.CONFIRMED;//潜伏者发病
            confirmedTime = MyPanel.worldTime;//刷新时间
        }
        //处理未隔离者的移动问题
        action();
        //处理健康人被感染的问题
        List<Person> people = PersonPool.getInstance().personList;
        if (state >= State.SHADOW) {
            return;
        }
        //通过一个随机值和安全距离决定感染其他人
        for (Person person : people) {
            if (person.getState() == State.NORMAL) {
                continue;
            }
            float random = new Random().nextFloat();
            if (random < Constants.BROAD_RATE && distance(person) < SAFE_DIST) {
                this.beInfected();
                break;
            }
        }
    }
}
