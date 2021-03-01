import {test} from "@lt.petuska/mpp-IR";

console.log("Hi");
test.sandbox.sayHello("Martynas");
const person: test.sandbox.Person = {
    name: "Yo",
    sureName: "Mama",
}
test.sandbox.sayFormalHello(person)

const extendedPerson: test.sandbox.ExtendedPerson = {
    name: "Should",
    sureName: "Still Work",
}
test.sandbox.sayFormalHello(extendedPerson)

const buggedPerson: test.sandbox.BuggedPerson = {
    getSafeName(): string {
        return 'Safe Word "fun"'
    },
    name: "Should",
    sureName: "Not Work"
}
test.sandbox.sayFormalBuggedHello(buggedPerson)

