# Photo Documentation Feature

The Photo Documentation feature provides electricians with a way to document their work with photos, annotations, and before/after comparisons.

## Features

### 1. Photo Capture and Management
- Take photos directly from the app
- Import photos from the device gallery
- Organize photos by job, date, and tags
- Add titles, descriptions, and tags to photos

### 2. Photo Annotation
- Add text annotations to photos
- Mark important areas with arrows, circles, or rectangles
- Highlight code violations or important details

### 3. Before/After Comparisons
- Create before/after pairs of photos
- View side-by-side comparisons
- Document progress and completed work

### 4. Geo-tagging
- Automatically tag photos with location data
- View photos on a map
- Filter photos by location

## Implementation Details

The feature follows the clean architecture pattern with:

1. **Domain Layer**
   - Models: `PhotoDocument`, `PhotoAnnotation`, `BeforeAfterPair`, etc.
   - Repository Interface: `PhotoDocRepository`
   - Use Cases: `SavePhotoDocumentUseCase`, `GetPhotoDocumentsByJobIdUseCase`, etc.

2. **Data Layer**
   - Repository Implementation: `PhotoDocRepositoryImpl`
   - Database Entities: `PhotoDocumentEntity`, `PhotoAnnotationEntity`, etc.
   - DAO: `PhotoDocDao`

3. **Presentation Layer**
   - ViewModel: `PhotoDocViewModel`
   - Fragments: `PhotoDocListFragment`, `PhotoDocDetailFragment`, etc.
   - Adapters: `PhotoDocAdapter`

## Future Enhancements

1. **Integration with Other Features**
   - Link photos to specific jobs
   - Attach photos to invoices and reports
   - Include photos in code violation checks

2. **Advanced Annotation**
   - Freehand drawing on photos
   - Measurement tools
   - Text recognition in photos

3. **Cloud Sync**
   - Backup photos to cloud storage
   - Share photos with team members or clients
   - Access photos across devices

4. **AI-Assisted Features**
   - Automatic detection of code violations
   - Automatic categorization of photos
   - Suggested annotations based on content
