import java.util.Random;

/**
 * 数学算法
 */
public class MathUtil {
    /**
     * 使用一个随机数生成器
     */
    private static final Random randomGen = new Random();

    /**
     * 标准正态分布化
     * u为流动意愿
     * 设X随机变量为服从正态分布，sigma是影响分布形态的系数 u值决定正态分布均值
     * 推导：
     * StdX = (X-u)/sigma
     * X = sigma * StdX + u
     */
    public static double stdGaussian(double sigma, double u) {
        double X = randomGen.nextGaussian();
        return sigma * X + u;
    }

}
