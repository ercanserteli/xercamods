package xerca.xercapaint.tests;

import net.minecraft.gametest.framework.GameTestHelper;

import java.util.List;

public class TestUtils {
    /**
     * Chains the given task with a delay of one tick between each task
     * @param helper The GameTestHelper of the running test
     * @param tasks The list of tasks to schedule
     * @return Returns the delay of the last task + 1 tick
     */
    public static int chainTasks(GameTestHelper helper, List<TestRunnable> tasks)
    {
        return chainTasks(helper, tasks, 0);
    }

    /**
     * Chains the given task with a delay of one tick between each task
     * @param helper The GameTestHelper of the running test
     * @param tasks The list of tasks to schedule
     * @param initialDelay The delay in ticks the task chain starts at
     * @return Returns the delay of the last task + 1 tick
     */
    public static int chainTasks(GameTestHelper helper, List<TestRunnable> tasks, int initialDelay)
    {
        int delay = initialDelay;
        for (TestRunnable task : tasks)
        {
            helper.runAfterDelay(delay, task);
            delay += task.getDuration();
        }
        return delay;
    }


    public interface TestRunnable extends Runnable {
        default int getDuration()
        {
            return 1;
        }
    }


    public record TestDelay(int delay) implements TestRunnable
    {
        @Override
        public void run() { }

        @Override
        public int getDuration() { return delay; }
    }
}
