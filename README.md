# FlickrSearch
Simple Android app to search for images on Flickr 

Enter your own API_KEY for Flickr in the Intent Service to test



The constraints for this project were to create a simple image search using the Flickr API.
-Update the results with each character change
    -This constraint is not good design because people type fast, and there's no need to issue a network call for each letter.
    -I tried to match the constraint but mitigate this issue by using an IntentService to handle the calls, 
    and only process the last call in the queue


Libraries Used:
OkHTTP for network calls
Picasso for fetching/caching bitmaps in the RecyclerAdapter.  This library is useful because it attemps the fetch 3 times before failing, it caches the images so you don't waste network calls on repeated images, and it handles the case where RecyclerView is updating the ViewModel.
