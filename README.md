# Backend Challenge
A RESTful API with some functions related to **bank account management** - [donus-code-challenge](https://github.com/devdigitalrepublic/donus-code-challenge/blob/master/backend.md)
    
## Routes
| Method		| URL 					| Authentication 		    		| Returns 					| Body 				| 
| --- 			| --- 					| ---					    		| --- 						| --- 				| 
| **GET**		| **/users** 			| hasRole("ADMIN")				    | All users					| 					| 
| **GET**		| **/users/user** 		| isAuthenticated() 			    | The authenticated user	|					| 
| **GET**		| **/users/user?cpf=**	| hasRole("ADMIN")				    | The specified user		| 					| 
| **POST**		| **/users** 			| permitAll()					    | The created user			| User JSON			| 
| **DELETE**	| **/users/user?cpf=** 	| hasRole("ADMIN")<br> or self user | 							| 					| 
| **PUT**		| **/deposit?cpf=** 	| isAuthenticated()			    	| The deposit extract		| Transaction JSON	| 
| **PUT**		| **/transfer?cpf=** 	| isAuthenticated()			    	| The transfer extract		| Transaction JSON	| 

## Body JSON

### User JSON:
```yaml
{
  "cpf": "063.742.250-31",
  "fullName": "Guilherme Cruz",
  "username": "cruzapi",
  "password": "test123"
}
```
CPF must be a valid [CPF](https://www.4devs.com.br/gerador_de_cpf), can be with or without punctuation. <br>
Password must have at least 4 chars, chars must be only digits, letters or common punctuation. <br>
### Transaction JSON: (Deposit/Transfer)
```yaml
{
  "amount": "100"
}
```
Amount must be positive and less than or equal to 2000.

## Params

- **/user?**, **/deposit?** and **/transfer?** support params:

| URL					| Type		| Description 				|
| ---					| ---		| --- 						|
| **/user?uuid=**		| UUID		| *Find user by UUID*		|
| **/user?cpf=**		| String	| *Find user by CPF*		|
| **/user?username=**	| String	| *Find user by Username*	|

## Author
| [<img src="https://github.com/cruzapi.png?size=115" width=115><br><sub>@CruzAPI</sub>](https://github.com/cruzapi) |
| :---: |

## Technology

- [Spring](https://spring.io/)
- [Maven](https://maven.apache.org/)
