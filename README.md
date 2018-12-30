# jpro-bikes

JPro Bikes

## Usage

   $ lein run

## URLs

Once the server is up & running these are the available URLs:

#### [No auth]

* [http://localhost:8080](/)             => Root

#### [Basic Auth (user: "guest" password: "password123")]

* [http://localhost:8080/bikes](/bikes)  => HTML version of the 5 nearest bike points to Leyton 
* [http://localhost:8080/bikes/json](/bikes/json)  => JSON version of the above

## Testing

   $ lein test

## TODO

* More testing
* Instead of a call to get all bike points every time, get once the nearer bike points and on successive calls get the details for those bike points only.
* Refactoring

## License

Distributed under the GNU General Public License (GPL) version 3
https://www.gnu.org/licenses/gpl.html
