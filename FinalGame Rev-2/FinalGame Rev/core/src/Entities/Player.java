package Entities;

import com.badlogic.gdx.graphics.Color;

public class Player implements Character {
    public static final float POISON_DURATION = 3.0f; // Duration of poison effect in seconds
    public static final float POISON_DAMAGE_PER_SECOND = 0.03f; // Damage per second when poisoned
    public static final float SLOW_DURATION = 2.5f;

    private float health;
    private float speed;
    private float poisonTimer;
    private float slowTimer;
    private boolean isPoisoned;
    private boolean isSlowed;

    public Player(float health, float speed) {
        this.health = health;
        this.speed = speed;
        this.poisonTimer = 0;
        this.slowTimer = 0;
        this.isPoisoned = false;
        this.isSlowed = false;
    }

    @Override
    public void update(float delta) {
        if (isPoisoned) {
            poisonTimer -= delta;
            health -= POISON_DAMAGE_PER_SECOND * delta;
            if (poisonTimer <= 0) {
                isPoisoned = false;
            }
            if (health <= 0) {
                health = 0;
            }
        }

        if (isSlowed) {
            slowTimer -= delta;
            speed = 100;
            if (slowTimer <= 0) {
                isSlowed = false;
                speed = 400;
            }
        }
    }

    public void applyPoison(float duration) {
        isPoisoned = true;
        poisonTimer = duration;
    }

    public void applySlow(float duration) {
        isSlowed = true;
        slowTimer = duration;
    }

    @Override
    public void takeDamage(float amount) {
        health -= amount;
        if (health <= 0) {
            health = 0;
        }
    }

    public void heal(float amount) {
        health += amount;
        if (health > 1) {
            health = 1;
        }
    }

    public float getHealth() {
        return health;
    }

    public float getSpeed() {
        return speed;
    }

    public Color getHealthBarColor() {
        if (isPoisoned) {
            return new Color(0, 1, 0, 1); // Green when poisoned
        } else if (isSlowed) {
            return new Color(0, 1, 1, 1); // Cyan when slowed
        } else {
            return new Color(1, 0, 0, 1); // Red otherwise
        }
    }
}