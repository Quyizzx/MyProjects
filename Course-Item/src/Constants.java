
public class Constants {

    public static int ORIGINAL_COUNT = 50;//初始感染人数
    public static float BROAD_RATE = 0.8f;//传播率
    public static float SHADOW_TIME = 140;//潜伏时间，14天对应140，比例为1：10
    public static int HOSPITAL_RECEIVE_TIME = 10;//医院收治响应时间
    public static int BED_COUNT = 1000;//医院床位
    /**
     * 流动意向平均值，建议调整范围：[-0.99,0.99]
     * <p>
     * -0.99 人群流动最慢速率，甚至完全控制疫情传播
     * 0.99为人群流动最快速率, 可导致全城感染
     */
    public static float u = -0.99f;//流动意向，-0.99为最慢速率，0.99为最快速率
    public static int CITY_PERSON_SIZE = 5000;//城市总人口数量
    public static float FATALITY_RATE = 0.50f;//病死率
    public static int DIE_TIME = 100;//死亡时间均值，30天，从发病（确诊）时开始计时
    public static double DIE_VARIANCE = 1;//死亡时间方差
    /**
     * 城市大小，窗口边界
     */
    public static final int CITY_WIDTH = 700;
    public static final int CITY_HEIGHT = 800;

}
