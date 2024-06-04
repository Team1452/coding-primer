package swervevisualizer;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.canvas.GraphicsContext;

/** This is a common pattern in game development.
 *
 * An Entity is something that participates in the global
 * tick loop (update and draw), and has children:
 * thus making a "scene hierarchy".
 * In this project, we have a "drivetrain" (Swerve.java)
 * that owns "modules" (SwerveModule.java) and the drivetrain
 * has physics "owned" by a Rigidbody. Although the Rigidbody
 * isn't drawn (the velocity arrow is drawn by SwerveModule)
 * it is still something that is dynamic (it does some physics
 * each tick). We also have Asteroids and Bullets! Which are
 * more a bonafide "game" than the swerve visualizer.
 *
 * I think having a base class like this is maybe a bit overkill.
 * You could, more simply, have update/draw methods in different
 * classes and explicitly handle where and when you call them.
 * But it's at least an interesting way to organize a game and
 * something worth seeing. (And for learning purposes
 * is a good example of polymorphism.) Though there are other patterns,
 * like Entity component system (ECS), which is to an extent more explicit
 * in consolidating the different components and behaviors of a game.
 */
public abstract class Entity {

  public class Rotation {

    double radians;

    public Rotation(double radians) {
      this.radians = radians;
    }

    public double getRadians() {
      return radians;
    }

    public double getDegrees() {
      return Math.toDegrees(radians);
    }
  }

  public class Transform {

    public Rotation rotation;
    public Vector2 translation;

    public void setRotation(Rotation rotation) {
      this.rotation = rotation;
    }

    public void setTranslation(Vector2 translation) {
      this.translation = translation;
    }

    public Rotation getRotation() {
      return rotation;
    }

    public Vector2 getTranslation() {
      return translation;
    }

    public void transform(GraphicsContext ctx) {
      ctx.rotate(rotation.getDegrees());
      ctx.translate(translation.x, translation.y);
    }
  }

  Entity parent;
  List<Entity> children = new ArrayList<>();
  private boolean active = true;

  public abstract Transform getTransform();

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public void adopt(Entity child) {
    if (child.hasParent()) {
      child.parent.removeChild(child);
    }

    addChild(child);
  }

  public void removeChild(Entity child) {
    if (child.parent != this) {
      throw new IllegalArgumentException(
        "Tried to remove entity as child that wasn't a child"
      );
    }
    child.parent = null;
    children.remove(child);
  }

  public boolean hasParent() {
    return parent != null;
  }

  public void addChild(Entity child) {
    if (child.hasParent()) {
      throw new IllegalArgumentException(
        "Tried to add entity as child that already has a parent"
      );
    }

    child.parent = this;
    children.add(child);
  }

  public void destroy() {
    if (hasParent()) {
      parent.removeChild(this);
    }
  }

  public Entity(Entity parent) {
    if (parent != null) {
      parent.addChild(this);
    }
    this.parent = parent;
  }

  /** .updateLocal and .drawLocal hold the actual
   * update and draw logic of the entity,
   * where .update and .draw make sure to call
   * .updateLocal and .drawLocal on the entity
   * and all its children.
   */
  protected abstract void updateLocal(double dt);

  protected abstract void drawLocal(GraphicsContext ctx);

  public void update(double dt) {
    if (active) {
      this.updateLocal(dt);

      for (Entity entity : children) {
        entity.update(dt);
      }
    }
  }

  public void draw(GraphicsContext ctx) {
    if (active) {
      this.drawLocal(ctx);

      for (Entity entity : children) {
        entity.draw(ctx);
      }
    }
  }
}
