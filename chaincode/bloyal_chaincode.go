/*
Copyright IBM Corp. 2016 All Rights Reserved.
*/

package main

import (
	"encoding/json"
	"fmt"
	"strings"
	"strconv"
	"github.com/hyperledger/fabric-chaincode-go/shim"
	pb "github.com/hyperledger/fabric-protos-go/peer"
	proto "github.com/hyperledger/fabric-samples/chaincode/proto"
)

// SimpleChaincode struct
type SimpleChaincode struct {
}

type user struct {
	ObjectType string `json:"docType"`
	Email 	   string `json:"email"`
	Password   string `json:"password"`
	Points 	   int	  `json:"points"`
}

// Server struct
type Server struct {

}

func main() {
	err := shim.Start(new(SimpleChaincode))
	fmt.Printf("Main entered!! ")
	if err != nil {
		fmt.Printf("Error starting Simple chaincode: %s", err)
	}
}

// Init initializes the chaincode
func (t *SimpleChaincode) Init(stub shim.ChaincodeStubInterface) pb.Response {
	fmt.Println("bloyal Init\n")
	return shim.Success(nil)
}

// Invoke invokes the needed functions
func (t *SimpleChaincode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	fmt.Println("bloyal Invoke\n")
	function, args := stub.GetFunctionAndParameters()
	fmt.Println("invoke is running " + function)

	if function == "addPoints" {
		return t.addPoints(stub, args)
	} else if function == "readPoints" {
		return t.readPoints(stub, args)
	} else if function == "logInUser" {
		return t.logInUser(stub, args)
	} else if function == "signUpUser" {
		return t.signUpUser(stub, args)
	}

	fmt.Println("invoke did not find func: " + function)
	return shim.Error("Received unknown function invocation")
}

//signUpUser takes email and pass, checks if email exists and awards it 0 points, return bool
func (t *SimpleChaincode) signUpUser(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	fmt.Println("bloyal signInUser\n")
	var email, password string
	var points int32
	var err error

	//    0       			1
	// "sara@gmail.com",  "sara",
	if len(args) != 2 {
		return shim.Error("Incorrect number of arguments. Expecting 2: email and password")
	}
	email := strings.ToLower(args[0])
	password := args[1]

	//go through blockchain and see if email matches any user
	userAsBytes, err := stub.GetState(email)
	if err != nil {
		return shim.Error("Error getting user: " + err.Error())
	} else if userAsBytes != nil {
		return shim.Error("Such email already exists") //return bool false
	}

	//Create user object and marshall to JSON
	objectType := "user"
	points := 0
	user := &user{objectType, email, password, points}
	userJSONasBytes, err := json.Marshal(user)
	if err != nil {
		return shim.Error(err.Error())
	}

	//Save user to the state
	err = stub.PutState(email, userJSONasBytes)
	if err != nil {
		return shim.Error(err.Error())
	}
	
	return shim.Success("New user added to blockchain") //returns bool true
}

//logInUser takes email and password, checks if email exists and pass is ok and sends bool back 
func (t *SimpleChaincode) logInUser(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	fmt.Println("bloyal logInUser\n")
	var email, password string
	var err error

	//    0       			1
	// "sara@gmail.com",  "sara",
	if len(args) != 2 {
		return shim.Error("Incorrect number of arguments. Expecting 2: email and password")
	}
	email := strings.ToLower(args[0])
	password := args[1]

	//go through blockchain and see if email and pass match any user
	userAsBytes, err := stub.GetState(email)
	if err != nil {
		return shim.Error("Error getting user: " + err.Error())
	} else if userAsBytes == nil {
		return shim.Error("Such email doesn't exists") //return bool false 
	}

	//check if password is correct
	userToCheckPassword := user{}
	err = json.Unmarshal(userAsBytes, &userToCheckPassword)
	if err != nil {
		return shim.Error(err.Error())
	}

	if userToCheckPassword.Password == password {
		fmt.Println("Login credentials are correct\n")
		return shim.Success(nil) //should return bool true here
	}

	return shim.Error("Email and password don't match any user") //return bool false
}

func (t *SimpleChaincode) addPoints(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	fmt.Println("bloyal addPoints\n")
	var err error
	var password string

	//    0       1
	// "sarap",  "2",
	if len(args) != 2 {
		return shim.Error("Incorrect number of arguments. Expecting 2: email and number of points")
	}

	// Input sanitation
	fmt.Println("- start adding points")
	if len(args[0]) <= 0 {
		return shim.Error("argument email must be a non-empty string")
	}
	if len(args[1]) <= 0 {
		return shim.Error("argument number of points must be a non-empty string")
	}

	email := strings.ToLower(args[0])
	noofpoints, err := strconv.Atoi(args[1])

	// Check if username exists 
	userAsBytes, err := stub.GetState(email)
	if err != nil {
		return shim.Error("Error occurred. Failed to get user: " + err.Error())
	} else if userAsBytes != nil {
		return shim.Error("Error, no such user: " + err.Error())
	} 

	//Update the point of a user
	userToUpdate := points{}
	err = json.Unmarshal(userAsBytes, &userToUpdate)
	if err != nil {
		return shim.Error(err.Error())
	}

	userToUpdate.Points += noofpoints //can this say +=?!?!???!?!?!!?!??!?!???!?!??!!
	password = userToUpdate.Password // we get the password used

	userAsBytes, _ := json.Marshal(userToUpdate)
	err = stub.PutState(username, userAsBytes)
	if err != nil {
		return shim.Error(err.Error())
	}

	return shim.Success(nil)  //return bool true
}

func (t *SimpleChaincode) readPoints(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	fmt.Println("bloyal readPoints\n")
	var err error
	var email string

	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Expecting email of the user")
	}

	email = strings.ToLower(args[0])
	userAsBytes, err := stub.GetState(email)
	if err != nil {
		return shim.Error("Error occured. Failed to get user ")
	} else if userAsBytes == nil {
		return shim.Error("Error, no such user: " + err.Error())
	}

	return shim.Success(userAsBytes.Points)
}