package io.github.noeppi_noeppi.mods.bongo.data;

import io.github.noeppi_noeppi.mods.bongo.Bongo;

public class TeamCompletion {
    
    private final int winningTasks;
    private final int winningAmount;

    public TeamCompletion(Bongo bongo, Team team) {
        int winningTasks = 0;
        int winningAmount = 0;
        for (int i = 0; i < 25; i++) {
            if (bongo.task(i).isWinningTask(team.completed(i), team.locked(i))) {
                winningTasks |= (1 << i);
                winningAmount += 1;
            }
        }
        this.winningTasks = winningTasks;
        this.winningAmount = winningAmount;
    }
    
    public int count() {
        return this.winningAmount;
    }
    
    public boolean has(int task) {
        return (this.winningTasks & (1 << (task % 25))) != 0;
    }
}
