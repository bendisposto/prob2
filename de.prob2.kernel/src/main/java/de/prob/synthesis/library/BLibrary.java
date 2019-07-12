package de.prob.synthesis.library;

import de.prob.prolog.output.IPrologTermOutput;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * A class representing a B/Event-B library configuration used for the synthesis backend.
 * Library components are operators like integer addition. Further options mainly concerning
 * performance of synthesis are available.
 */
@SuppressWarnings("unused")
public class BLibrary {

  private static final int MAXIMUM_LIBRARY_EXPANSION = 15;

  private final Set<LibraryComponent> predicates = new HashSet<>();
  private final Set<LibraryComponent> sets = new HashSet<>();
  private final Set<LibraryComponent> numbers = new HashSet<>();
  private final Set<LibraryComponent> relations = new HashSet<>();
  private final Set<LibraryComponent> sequences = new HashSet<>();
  private final Set<LibraryComponent> substitutions = new HashSet<>();

  private ConsiderIfType considerIfStatements = ConsiderIfType.NONE;
  /**
   * Automatically configure the library during synthesis if is true.
   */
  private boolean useDefaultLibrary = true;
  /**
   * Level of library expansion for the automatic configuration.
   */
  private int defaultLibraryExpansion = 1;
  /**
   * Consider constants that have to be enumerated by the solver during synthesis if is true.
   */
  private boolean enumerateConstants = true;


  /**
   * Update the amount of a specific component. If the component is currently not part of the
   * library it will be added and its amount is set to be equal to the parameter addAmount.
   *
   * @param libraryComponentName A specific library component.
   * @param addAmount            The amount that is added to the component's amount
   *                             (can be either positive or negative).
   */
  public void updateComponentAmount(final LibraryComponentName libraryComponentName,
                                    final int addAmount) {
    final LibraryComponent libraryComponent = new LibraryComponent(libraryComponentName, 0);
    final Set<LibraryComponent> componentSet =
        getComponentSetForType(libraryComponent.getComponentType());
    updateComponentAmount(componentSet, libraryComponent, addAmount);
  }

  /**
   * Explicitly set the amount of a specific component. If the component is currently not part of
   * the library it will be added and its amount is set to be equal to the parameter amount.
   *
   * @param libraryComponentName A specific library component.
   * @param amount               The amount that is added to the component's amount
   *                             (can be either positive or negative).
   */
  public void setComponentAmount(final LibraryComponentName libraryComponentName,
                                 final int amount) {
    final LibraryComponent libraryComponent = new LibraryComponent(libraryComponentName, 0);
    final Set<LibraryComponent> componentSet =
        getComponentSetForType(libraryComponent.getComponentType());
    setComponentAmount(componentSet, libraryComponent, amount);
  }

  private Set<LibraryComponent> getComponentSetForType(final LibraryComponentType componentType) {
    switch (componentType) {
      case PREDICATES:
        return predicates;
      case SETS:
        return sets;
      case NUMBERS:
        return numbers;
      case RELATIONS:
        return relations;
      case SEQUENCES:
        return sequences;
      case SUBSTITUTIONS:
        return substitutions;
      default:
        throw new UnsupportedOperationException("Library component is not supported.");
    }
  }

  private void updateComponentAmount(final Set<LibraryComponent> components,
                                     final LibraryComponent libraryComponent,
                                     final int addAmount) {
    final Optional<LibraryComponent> optionalLibraryComponent = components.stream()
        .filter(libraryComponent::equals).findFirst();
    if (optionalLibraryComponent.isPresent()) {
      final LibraryComponent existing = optionalLibraryComponent.get();
      final int preAmount = existing.getAmount();
      existing.setAmount(preAmount + addAmount);
      return;
    }
    libraryComponent.setAmount(addAmount);
    components.add(libraryComponent);
  }

  private void setComponentAmount(final Set<LibraryComponent> components,
                                  final LibraryComponent libraryComponent,
                                  final int amount) {
    final Optional<LibraryComponent> optionalLibraryComponent = components.stream()
        .filter(libraryComponent::equals).findFirst();
    if (optionalLibraryComponent.isPresent()) {
      final LibraryComponent existing = optionalLibraryComponent.get();
      final int preAmount = existing.getAmount();
      existing.setAmount(amount);
      return;
    }
    libraryComponent.setAmount(amount);
    components.add(libraryComponent);
  }

  /**
   * Remove a {@link LibraryComponent} completely from the library.
   */
  public void removeLibraryComponent(final LibraryComponentName libraryComponentName) {
    final LibraryComponent libraryComponent = new LibraryComponent(libraryComponentName, 0);
    final Set<LibraryComponent> componentSet =
        getComponentSetForType(libraryComponent.getComponentType());
    componentSet.remove(libraryComponent);
  }

  /**
   * Add a {@link LibraryComponent} to the library. If it already exists in the library, its amount
   * is increased. Note, a single component has a unique output within a program. For instance,
   * it is necessary to use two addition components to achieve a behavior like "a + b + 1".
   */
  public void addLibraryComponent(final LibraryComponentName libraryComponentName) {
    final LibraryComponent libraryComponent = new LibraryComponent(libraryComponentName, 1);
    final Set<LibraryComponent> componentSet =
        getComponentSetForType(libraryComponent.getComponentType());
    addComponentOrIncreaseAmount(componentSet, libraryComponent);
  }

  /**
   * Add a component to the library defined by the component set property if it does not already
   * exist.
   */
  private void addComponentOrIncreaseAmount(final Set<LibraryComponent> components,
                                            final LibraryComponent libraryComponent) {
    final Optional<LibraryComponent> optionalLibraryComponent = components.stream()
        .filter(libraryComponent::equals).findFirst();
    if (!optionalLibraryComponent.isPresent()) {
      components.add(libraryComponent);
      return;
    }
    optionalLibraryComponent.get().increaseAmount();
  }

  /**
   * Set the index of the (internally) predefined library configuration to be used.
   */
  public void setLibraryExpansion(final int libraryExpansion) {
    defaultLibraryExpansion = libraryExpansion;
  }

  /**
   * Use the next predefined library configuration. Return true if a predefined configuration is
   * left, otherwise false.
   */
  public boolean expandDefaultLibrary() {
    final int libraryExpansion = defaultLibraryExpansion;
    if (libraryExpansion >= MAXIMUM_LIBRARY_EXPANSION || !useDefaultLibrary) {
      defaultLibraryExpansion = 1;
      return false;
    }
    defaultLibraryExpansion = libraryExpansion + 1;
    return true;
  }

  /**
   * Set true to use a set of predefined library configurations. If is true, possibly manually
   * selected components are ignored (but not deleted).
   */
  public void setUseDefaultLibrary(final boolean useDefaultLibrary) {
    this.useDefaultLibrary = useDefaultLibrary;
  }

  /**
   * Set true to consider constants that are enumerated by the constraint solver. Otherwise, only
   * machine constants are used.
   */
  public void setEnumerateConstants(final boolean enumerateConstants) {
    this.enumerateConstants = enumerateConstants;
  }

  /**
   * Print the selected library components to a {@link IPrologTermOutput prolog term} or, for
   * instance, default:1 for a default library configuration at level 1 of its predefined
   * expansions.
   */
  public void printToPrologTerm(final IPrologTermOutput pto) {
    if (useDefaultLibrary) {
      pto.openTerm(":")
          .printAtom("default")
          .printNumber(defaultLibraryExpansion)
          .closeTerm();
      return;
    }
    pto.openList()
        .openTerm("predicates").openList();
    getPredicates().forEach(libraryComponent -> libraryComponent.printToPrologList(pto));
    pto.closeList().closeTerm()
        .openTerm("numbers").openList();
    getNumbers().forEach(libraryComponent -> libraryComponent.printToPrologList(pto));
    pto.closeList().closeTerm()
        .openTerm("relations").openList();
    getRelations().forEach(libraryComponent -> libraryComponent.printToPrologList(pto));
    pto.closeList().closeTerm()
        .openTerm("sequences").openList();
    getSequences().forEach(libraryComponent -> libraryComponent.printToPrologList(pto));
    pto.closeList().closeTerm()
        .openTerm("sets").openList();
    getSets().forEach(libraryComponent -> libraryComponent.printToPrologList(pto));
    pto.closeList().closeTerm()
        .openTerm("substitutions").openList();
    getSubstitutions().forEach(libraryComponent -> libraryComponent.printToPrologList(pto));
    pto.closeList().closeTerm()
        .closeList();
  }

  /**
   * Return true if there are no selected library components and the default predefined libraries
   * should not be used.
   */
  public boolean isEmpty() {
    return !useDefaultLibrary
        && predicates.isEmpty()
        && sets.isEmpty()
        && numbers.isEmpty()
        && sequences.isEmpty()
        && relations.isEmpty()
        && substitutions.isEmpty();
  }

  public ConsiderIfType considerIfStatements() {
    return considerIfStatements;
  }

  public boolean useDefaultLibrary() {
    return useDefaultLibrary;
  }

  public boolean enumerateConstants() {
    return enumerateConstants;
  }

  public Set<LibraryComponent> getPredicates() {
    return predicates;
  }

  public Set<LibraryComponent> getSets() {
    return sets;
  }

  @SuppressWarnings("WeakerAccess")
  public Set<LibraryComponent> getNumbers() {
    return numbers;
  }

  @SuppressWarnings("WeakerAccess")
  public Set<LibraryComponent> getRelations() {
    return relations;
  }

  @SuppressWarnings("WeakerAccess")
  public Set<LibraryComponent> getSequences() {
    return sequences;
  }

  @SuppressWarnings("WeakerAccess")
  public Set<LibraryComponent> getSubstitutions() {
    return substitutions;
  }

  public int getLibraryExpansion() {
    return defaultLibraryExpansion;
  }
}
