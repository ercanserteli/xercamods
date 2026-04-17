package xerca.xercamusic.client;

import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

public class NoteSound extends AbstractSoundInstance implements TickableSoundInstance {
    private static final float[] FADE_VOLUMES = {0.0f, 0.02f, 0.12f, 0.3f};
    private final float originalVolume;
    private final float originalPitch;
    private float dynamicVolume = -1f;  // -1 means no dynamic override

    private boolean donePlaying;
    private int remainingTicks = -1;

    // Glissando (pitch slide) - supports multi-point
    private float[] pitchWaypoints;   // null means no glissando; array of target pitches for each segment
    private float[] waypointPositions; // null = evenly spaced; values 0.0-1.0 indicating when each waypoint is reached
    private int glissandoTotalTicks;
    private int glissandoTicksElapsed;

    NoteSound(SoundEvent soundEvent, SoundSource category, float x, float y, float z, float volume, float pitch, int lengthTicks) {
        super(soundEvent, category, RandomSource.create());
        this.volume = this.originalVolume = volume;
        this.pitch = this.originalPitch = pitch;
        this.x = x;
        this.y = y;
        this.z = z;
        this.looping = false;
        this.attenuation = Attenuation.LINEAR;
        if (lengthTicks > 0) {
            this.remainingTicks = lengthTicks + 3;
        }
    }

    public void stopSound() {
        remainingTicks = 3;
    }

    /**
     * Update the volume dynamically (e.g., for crescendo/decrescendo).
     * Stored separately so tick() can apply it properly alongside fade-out.
     */
    public void setDynamicVolume(float newVolume) {
        this.dynamicVolume = newVolume;
    }

    /**
     * Enable single-point glissando (smooth pitch slide) to target pitch.
     */
    public void setGlissando(float targetPitch, int durationTicks) {
        this.pitchWaypoints = new float[] { targetPitch };
        this.glissandoTotalTicks = Math.max(1, durationTicks);
        this.glissandoTicksElapsed = 0;
    }

    /**
     * Enable multi-point glissando. Waypoints are evenly distributed across the duration.
     * Segment 0: originalPitch → waypoints[0], Segment 1: waypoints[0] → waypoints[1], etc.
     */
    public void setGlissando(float[] targetPitches, int durationTicks) {
        this.pitchWaypoints = targetPitches;
        this.waypointPositions = null;
        this.glissandoTotalTicks = Math.max(1, durationTicks);
        this.glissandoTicksElapsed = 0;
    }

    /**
     * Enable multi-point glissando with custom timing positions.
     * @param targetPitches array of target pitches for each waypoint
     * @param positions     parallel array of fractional positions (0.0-1.0) when each waypoint is reached; null = evenly spaced
     * @param durationTicks total glissando duration
     */
    public void setGlissando(float[] targetPitches, float[] positions, int durationTicks) {
        this.pitchWaypoints = targetPitches;
        this.waypointPositions = (positions != null && positions.length == targetPitches.length) ? positions : null;
        this.glissandoTotalTicks = Math.max(1, durationTicks);
        this.glissandoTicksElapsed = 0;
    }

    @Override
    public boolean isStopped() {
        return this.donePlaying;
    }

    @Override
    public void tick() {
        if (remainingTicks == 0) {
            donePlaying = true;
            remainingTicks = -1;
        }
        if(remainingTicks > 0){
            float baseVolume = dynamicVolume >= 0f ? dynamicVolume : originalVolume;
            volume = baseVolume * (remainingTicks >= FADE_VOLUMES.length ? 1 : FADE_VOLUMES[remainingTicks]);
            remainingTicks--;
        } else if(dynamicVolume >= 0f) {
            // No length timer, but dynamic volume is set, therefore we should apply it
            volume = dynamicVolume;
        }

        // Glissando: smoothly interpolate pitch through waypoints
        if(pitchWaypoints != null && pitchWaypoints.length > 0 && glissandoTicksElapsed < glissandoTotalTicks) {
            glissandoTicksElapsed++;
            float progress = (float)glissandoTicksElapsed / (float)glissandoTotalTicks;
            int numWaypoints = pitchWaypoints.length;

            int segIdx;
            float localProgress;

            if (waypointPositions != null && waypointPositions.length == numWaypoints) {
                // Custom-positioned waypoints: find segment based on position thresholds
                segIdx = 0;
                for (int i = 0; i < numWaypoints; i++) {
                    if (progress <= waypointPositions[i]) {
                        segIdx = i;
                        break;
                    }
                    segIdx = i;
                }
                float segStart = segIdx == 0 ? 0.0f : waypointPositions[segIdx - 1];
                float segEnd = waypointPositions[segIdx];
                float segLen = segEnd - segStart;
                localProgress = segLen > 0.0001f ? Math.min((progress - segStart) / segLen, 1.0f) : 1.0f;
                // If past the last waypoint position, hold the final pitch
                if (progress > waypointPositions[numWaypoints - 1]) {
                    this.pitch = pitchWaypoints[numWaypoints - 1];
                    return;
                }
            } else {
                // Even distribution (original behavior)
                float scaledProgress = progress * numWaypoints;
                segIdx = Math.min((int)scaledProgress, numWaypoints - 1);
                localProgress = scaledProgress - segIdx;
                if (segIdx >= numWaypoints - 1) {
                    localProgress = Math.min(localProgress, 1.0f);
                }
            }

            float fromPitch = segIdx == 0 ? originalPitch : pitchWaypoints[segIdx - 1];
            float toPitch = pitchWaypoints[segIdx];
            // Exponential interpolation for musical pitch (sounds linear to human ear)
            if (Math.abs(fromPitch - toPitch) < 0.0001f) {
                this.pitch = toPitch;
            } else {
                this.pitch = fromPitch * (float)Math.pow(toPitch / fromPitch, localProgress);
            }
        }
    }
}
