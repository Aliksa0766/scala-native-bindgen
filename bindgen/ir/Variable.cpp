#include "Variable.h"
#include "../Utils.h"

Variable::Variable(const std::string &name, std::shared_ptr<const Type> type)
    : TypeAndName(name, type) {}

llvm::raw_ostream &operator<<(llvm::raw_ostream &s, const Variable &variable) {
    s << "  val " << variable.getName() << ": " << variable.getType()->str()
      << " = native.extern\n";
    return s;
}

bool Variable::hasIllegalUsageOfOpaqueType() const {
    return isAliasForOpaqueType(type.get());
}
