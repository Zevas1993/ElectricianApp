# NEC Code Lookup Feature

The NEC Code Lookup feature provides electricians with easy access to the National Electrical Code (NEC) articles, updates, and violation checks.

## Features

### 1. Code Search
- Search for NEC articles by text, category, or year
- View detailed article content, including the full text and summary
- Browse articles by category

### 2. Code Updates
- View changes between different NEC editions (e.g., 2017 to 2020)
- Filter updates by impact level (minor, moderate, significant)

### 3. Code Violation Checks
- Check installations against NEC requirements
- Input parameters and get immediate feedback on compliance
- Receive detailed explanations of violations

### 4. Bookmarks
- Save frequently referenced articles
- Add personal notes to bookmarked articles
- Quickly access saved articles

## Implementation Details

The feature follows the clean architecture pattern with:

1. **Domain Layer**
   - Models: `NecArticle`, `NecCodeUpdate`, `CodeViolationCheck`, etc.
   - Repository Interface: `NecCodeRepository`
   - Use Cases: `NecCodeSearchUseCase`, `GetNecArticleByIdUseCase`, etc.

2. **Data Layer**
   - Repository Implementation: `NecCodeRepositoryImpl`
   - Database Entities: `NecArticleEntity`, `NecCodeUpdateEntity`, etc.
   - DAO: `NecCodeDao`
   - Sample Data Provider: `NecCodeDataProvider`

3. **Presentation Layer**
   - ViewModel: `NecCodeViewModel`
   - Fragments: `NecCodeLookupFragment`, `NecCodeSearchFragment`, etc.
   - Adapters: `NecArticleAdapter`

## Future Enhancements

1. **Offline Access**
   - Download complete NEC database for offline use
   - Sync bookmarks and notes across devices

2. **Advanced Search**
   - Full-text search with highlighting
   - Search history and suggestions

3. **Interpretation Guides**
   - Add practical interpretation guides for complex code articles
   - Include diagrams and examples

4. **Code Comparison**
   - Side-by-side comparison of articles between different NEC editions
   - Highlight specific changes

5. **Integration with Other Features**
   - Link relevant code articles to calculator results
   - Suggest applicable code articles based on job details
