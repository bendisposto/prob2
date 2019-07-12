package de.prob.synthesis.library;

import de.prob.prolog.output.IPrologTermOutput;

import java.util.Objects;
import java.util.stream.IntStream;

/**
 * A container for a library component to configure the used library for the synthesis tool. The
 * combination of component name and type is unique.
 */
public class LibraryComponent {

  private final LibraryComponentName componentName;
  private final LibraryComponentType componentType;
  private final String componentDescription;
  private final String internalName;
  private final String syntax;

  private int amount;

  public LibraryComponent(final LibraryComponentName componentName,
                          final int amount) {
    this.componentName = componentName;
    this.componentType = LibraryComponentMeta.libraryComponentTypes.get(componentName);
    this.componentDescription = LibraryComponentMeta.libraryComponentDescrs.get(componentName);
    this.internalName = LibraryComponentMeta.libraryComponentInternalNames.get(componentName);
    this.syntax = LibraryComponentMeta.libraryComponentSyntax.get(componentName);
    this.amount = amount;
  }

  /**
   * Increase the amount the library component should be used by one.
   */
  public void increaseAmount() {
    amount++;
  }

  /**
   * Decrease the amount the library component should be used by one.
   */
  public void decreaseAmount() {
    final int newValue = amount - 1;
    if (newValue < 0) {
      amount = 0;
      return;
    }
    amount = newValue;
  }

  void resetAmount() {
    amount = 0;
  }

  public String getComponentDescription() {
    return componentDescription;
  }

  public LibraryComponentName getComponentName() {
    return componentName;
  }

  public String getSyntax() {
    return syntax;
  }

  public LibraryComponentType getComponentType() {
    return componentType;
  }

  public int getAmount() {
    return amount;
  }

  public void setAmount(final int amount) {
    this.amount = amount;
  }

  private String getInternalName() {
    return internalName;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    LibraryComponent that = (LibraryComponent) obj;
    return Objects.equals(componentName, that.componentName)
        && componentType == that.componentType;
  }

  @Override
  public int hashCode() {
    return Objects.hash(componentName, componentType);
  }

  @Override
  public String toString() {
    return componentDescription;
  }

  void printToPrologList(final IPrologTermOutput pto) {
    IntStream.range(0, amount).forEach(value -> pto.printAtom(getInternalName()));
  }
}
