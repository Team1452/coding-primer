package swervevisualizer;

import javafx.scene.canvas.GraphicsContext;

/**
 * Owns pose (position + heading) of object that
 * is affected by inertia/physics, approximating
 * a point particle. Handles basic physics simulation.
 */
public class Rigidbody {

  private Vector2 position;
  private double headingRadians;

  // Momentum
  private final double mass;
  private Vector2 velocity = new Vector2(0, 0);

  // Angular momentum
  private final double momentOfInertia;
  private double angularVelocity = 0;

  public Rigidbody(
    Vector2 position,
    double headingRadians,
    double mass,
    double momentOfInertia
  ) {
    this.position = position;
    this.headingRadians = headingRadians;
    this.mass = mass;
    this.momentOfInertia = momentOfInertia;
  }

  public void setVelocity(Vector2 velocity) {
    this.velocity = velocity;
  }

  public void setAngularVelocity(double angularVelocity) {
    this.angularVelocity = angularVelocity;
  }

  public void setPosition(Vector2 position) {
    this.position = position;
  }

  public void update(double dt) {
    position = position.plus(velocity.times(dt));
    headingRadians =
      Utils.angleModulusRadians(headingRadians + angularVelocity * dt);
  }

  public void applyFriction(double friction, double dt) {
    // The "physics" are pretty crude: ex. FRICTION is just
    // a dampening term that slows the robot over time.
    // To be more realistic, you'd calculate this based on the momentum
    // of the wheel, and the internal FRICTION/dampening would be from the gearbox, back-EMF,
    // and the contact between the tread and the floor.
    angularVelocity -= friction * angularVelocity * dt;
    velocity = velocity.plus(velocity.times(-friction * dt));
  }

  public void applyImpulse(Vector2 force, double torque, double dt) {
    angularVelocity = angularVelocity + torque / momentOfInertia * dt;
    velocity = velocity.plus(force.times(dt / mass));
  }

  public void translate(GraphicsContext ctx) {
    ctx.translate(position.x, position.y);
  }

  public void rotate(GraphicsContext ctx) {
    ctx.rotate(headingRadians);
  }

  public double getHeadingRadians() {
    return headingRadians;
  }

  public double getHeadingDegrees() {
    return Math.toDegrees(headingRadians);
  }

  public Vector2 getHeadingVector() {
    return Vector2.fromPolar(1, headingRadians);
  }

  public Vector2 getVelocity() {
    return velocity;
  }

  public Vector2 getPosition() {
    return position;
  }
}
