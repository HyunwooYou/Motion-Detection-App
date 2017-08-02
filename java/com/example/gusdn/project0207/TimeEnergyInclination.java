package com.example.gusdn.project0207;

/**
 * Created by Hyun woo on 2017-02-10.
 *
 * 시간, 에너지, 기울기 정보를 담고있는 클래스
 */
class TimeEnergyInclination{
    private int time;
    private int energy;
    private int inclination;

    public void setTime(int time) { this.time = time; }
    public void setEnergy(int energy) { this.energy = energy; }
    public void setInclination(int inclination) { this.inclination = inclination; }

    public int getTime() { return time; }
    public int getEnergy() { return energy; }
    public int getInclination() { return inclination; }
}