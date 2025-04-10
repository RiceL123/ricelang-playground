/*
 * IdEntry.java
 * Represents an entry in the symbol table for an identifier.
 */

package VC.Checker;

import VC.ASTs.Decl;

import java.util.Objects;

/**
 * Represents an entry in the symbol table for an identifier.
 * Each entry contains the identifier's name, its associated declaration,
 * the scope level, and a reference to the previous entry in the symbol table.
 */
public final class IdEntry {

    protected final String id;            // The identifier's name
    protected final Decl attr;            // The associated declaration
    protected final int level;            // The scope level of the identifier
    protected final IdEntry previousEntry; // Reference to the previous entry in the symbol table

    /**
     * Constructs a new IdEntry.
     *
     * @param id            The identifier's name.
     * @param attr          The associated declaration.
     * @param level         The scope level of the identifier.
     * @param previousEntry The previous entry in the symbol table.
     */
    public IdEntry(String id, Decl attr, int level, IdEntry previousEntry) {
        this.id = Objects.requireNonNull(id, "Identifier must not be null");
        this.attr = Objects.requireNonNull(attr, "Declaration must not be null");
        this.level = level;
        this.previousEntry = previousEntry;
    }

    @Override
    public String toString() {
        return String.format("IdEntry{id='%s', attr=%s, level=%d, previousEntry=%s}",
                id, attr, level, previousEntry != null ? previousEntry.id : "null");
    }
}
