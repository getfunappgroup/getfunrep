package getfun.com.br.getfun.provider;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by viniciusthiengo on 6/21/15.
 */
public class SearchableProvider extends SearchRecentSuggestionsProvider {
    public static final String AUTHORITY = "getfun.com.br.getfun.provider.SearchableProvider";
    public static final int MODE = DATABASE_MODE_QUERIES;

    public SearchableProvider(){
        setupSuggestions( AUTHORITY, MODE );
    }
}
