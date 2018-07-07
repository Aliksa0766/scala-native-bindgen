#include "Function.h"
#include "../Utils.h"

Parameter::Parameter(std::string name, std::shared_ptr<Type> type)
    : TypeAndName(std::move(name), type) {}

Function::Function(const std::string &name, std::vector<Parameter *> parameters,
                   std::shared_ptr<Type> retType, bool isVariadic)
    : name(name), scalaName(name), parameters(std::move(parameters)),
      retType(std::move(retType)), isVariadic(isVariadic) {}

llvm::raw_ostream &operator<<(llvm::raw_ostream &s, const Function &func) {
    if (func.scalaName != func.name) {
        s << "  @native.link(\"" << func.name << "\")\n";
    }
    s << "  def " << handleReservedWords(func.scalaName) << "(";
    std::string sep = "";
    for (const auto &param : func.parameters) {
        s << sep << handleReservedWords(param->getName()) << ": "
          << param->getType()->str();
        sep = ", ";
    }
    if (func.isVariadic) {
        /* the C Iso require at least one argument in a variadic function, so
         * the comma is fine */
        s << ", " << func.getVarargsParameterName() << ": native.CVararg*";
    }
    s << "): " << func.retType->str() << " = native.extern\n";
    return s;
}

bool Function::usesType(std::shared_ptr<Type> type, bool stopOnTypeDefs) const {
    if (*retType == *type || retType.get()->usesType(type, stopOnTypeDefs)) {
        return true;
    }
    for (const auto &parameter : parameters) {
        if (*parameter->getType() == *type ||
            parameter->getType().get()->usesType(type, stopOnTypeDefs)) {
            return true;
        }
    }
    return false;
}

std::string Function::getName() const { return name; }

std::string Function::getVarargsParameterName() const {
    std::string parameterName = "varArgs";
    int i = 0;
    while (existsParameterWithName(parameterName)) {
        parameterName = "varArgs" + std::to_string(i++);
    }
    return parameterName;
}

bool Function::existsParameterWithName(const std::string &parameterName) const {
    for (const auto &parameter : parameters) {
        if (parameter->getName() == parameterName) {
            return true;
        }
    }
    return false;
}

void Function::setScalaName(std::string scalaName) {
    this->scalaName = std::move(scalaName);
}

Function::~Function() {
    for (const auto &parameter : parameters) {
        delete parameter;
    }
}
