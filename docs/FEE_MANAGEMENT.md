# SSAMS Fee Management

Fee management is independent of the payment gateway.

## Hierarchy
Institution -> Academic Year -> Program/Class -> Fee Category -> Fee Structure -> Student Assignment -> Invoice -> Payment -> Allocation -> Ledger

## Fee categories
Examples include tuition, admission, examination, library, laboratory, transport, hostel and miscellaneous. These must remain configurable.

## Fee structure
Recommended fields:
- institution
- academic year
- scope/class/program
- fee category
- amount
- currency
- frequency
- due date
- active

## Student fee assignment
The backend determines fees applicable to a student. Clients must not invent payable amounts.

## Invoice
Recommended states:
DRAFT, ISSUED, PARTIALLY_PAID, PAID, OVERDUE, CANCELLED.

## Partial payment
If institution policy permits:
outstanding = invoice total - successful allocations - valid credits

Use precise monetary types; never floating point for financial calculations.

## Discounts/waivers
Represent them as explicit adjustments. Do not overwrite the original fee silently.

## Gateway independence
Invoice creation, balance calculation and ledger reporting must work even when eSewa is unavailable.
