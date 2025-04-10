# Material Management Feature

The Material Management feature provides electricians with a comprehensive system to track materials, manage inventory, and generate material lists for jobs.

## Features

### 1. Material Inventory
- Track materials, quantities, and locations
- Set minimum stock levels and get low stock alerts
- Record material transactions (purchases, usage, returns)
- View transaction history for each material

### 2. Material Lists
- Create material lists for jobs
- Generate lists from calculations or templates
- Track material allocation and usage for jobs
- Export material lists for ordering

### 3. Supplier Management
- Store supplier information and contact details
- Record price quotes from different suppliers
- Compare prices across suppliers
- Mark preferred suppliers for different material categories

## Implementation Details

The feature follows the clean architecture pattern with:

1. **Domain Layer**
   - Models: `Material`, `MaterialList`, `MaterialListItem`, `Supplier`, etc.
   - Repository Interface: `MaterialRepository`
   - Use Cases: `GetAllMaterialsUseCase`, `SaveMaterialUseCase`, etc.

2. **Data Layer**
   - Repository Implementation: `MaterialRepositoryImpl`
   - Database Entities: `MaterialEntity`, `MaterialListEntity`, etc.
   - DAO: `MaterialDao`

3. **Presentation Layer**
   - ViewModels: `MaterialListViewModel`, `MaterialDetailViewModel`, etc.
   - Fragments: `MaterialListFragment`, `MaterialDetailFragment`, etc.
   - Adapters: `MaterialListAdapter`

## Future Enhancements

1. **Integration with Other Features**
   - Link materials to specific jobs
   - Generate material lists from calculations
   - Include materials in invoices and reports

2. **Advanced Inventory Management**
   - Barcode scanning for quick material lookup
   - QR code generation for materials and locations
   - Automated reorder suggestions

3. **Supplier Integration**
   - Direct ordering from suppliers
   - Price comparison across suppliers
   - Order tracking and history

4. **Reporting**
   - Material usage reports by job
   - Cost analysis reports
   - Inventory valuation reports
