// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.vision;

/** Add your docs here. */
public class VisionLimeLightSim extends VisionSystem {

    public VisionLimeLightSim(String name, int colIndex) {
        super(name, colIndex);
    }
    
    @Override
    public void setPipeline(double pipeline) {}

    @Override
    public void updateViaNetworkTable(String path) {
    }

    @Override
    public void setLight(boolean on) {
    }

    @Override
    public boolean isLightOn() {
        return false;
    }

    @Override
    public void flashLight() {
    }

    @Override
    public void setSnapshotMode(int snapVal) {
    }
    
}
