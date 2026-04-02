package com.example.foodorder.model;

import java.math.BigDecimal;

/**
 * Interface: Billable
 *
 * OOP concept: Interface
 *   Defines a contract → any entity that can be priced and included
 *   on a bill must implement calculateTotal().
 *
 *   Implemented by:
 *     - CartItem  (price × quantity)
 *     - Order     (sum of all CartItems)
 */
public interface Billable {

    /**
     * Calculate and return the monetary total for this billable entity.
     * @return total as an exact-precision BigDecimal
     */
    BigDecimal calculateTotal();
}
