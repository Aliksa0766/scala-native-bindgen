#ifndef SCALA_NATIVE_BINDGEN_INTERMEDIATEREPRESENTATION_H
#define SCALA_NATIVE_BINDGEN_INTERMEDIATEREPRESENTATION_H

#include "Enum.h"
#include "Function.h"
#include "LiteralDefine.h"
#include "Struct.h"
#include "TypeDef.h"

/**
 * Intermediate representation
 */
class IR {
  public:
    explicit IR(std::string libName, std::string linkName,
                std::string objectName, std::string packageName);

    void addFunction(std::string name, std::vector<Parameter> parameters,
                     std::string, bool isVariadic);

    void addTypeDef(std::string name, std::string type);

    void addEnum(std::string name, std::string type,
                 std::vector<Enumerator> enumerators);

    void addStruct(std::string name, std::vector<Field> fields,
                   uint64_t typeSize);

    void addUnion(std::string name, std::vector<Field> fields,
                  uint64_t maxSize);

    void addLiteralDefine(std::string name, std::string literal,
                          std::string type);

    /**
     * @return true if there are no functions, types,
     *         structs and unions
     */
    bool libObjEmpty() const;

    bool hasEnums() const;

    friend llvm::raw_ostream &operator<<(llvm::raw_ostream &s, const IR &ir);

    void generate(const std::string &excludePrefix);

    void removeDefine(const std::string &name);

  private:
    /**
     * Generates type defs for enums, structs and unions
     */
    void generateTypeDefs();

    /**
     * @return true if helper methods will be generated for this library
     */
    bool hasHelperMethods() const;

    /**
     * Remove functions that start with given prefix.
     *
     * Replace typedef by underlying type if it starts with given prefix
     * and it is used only in other typedefs.
     *
     * Example:
     * @code
     * type __int32_t = native.CInt
     * type __darwin_pid_t = __int32_t
     * type pid_t = __darwin_pid_t
     * @endcode
     *
     * Becomes:
     * @code
     * type pid_t = native.CInt
     * @endcode
     *
     */
    void filterDeclarations(const std::string &excludePrefix);

    /**
     * Remove all typedefs that start with given prefix.
     */
    void filterTypeDefs(const std::string &excludePrefix);

    /**
     * Find all typedefs that use oldType and replace it with newType.
     */
    void replaceTypeInTypeDefs(const std::string &oldType,
                               const std::string &newType);

    /**
     * @return true if given type is used only in typedefs.
     */
    bool typeIsUsedOnlyInTypeDefs(std::string type);

    /**
     * @return true if type is used in one of given declarations.
     */
    template <typename T>
    bool isTypeUsed(const std::vector<T> &declarations,
                    const std::string &type);

    void setScalaNames();

    bool existsFunctionWithName(std::string functionName);

    template <typename T>
    void filter(std::vector<T> &declarations, const std::string &excludePrefix);

    std::string libName;    // name of the library
    std::string linkName;   // name of the library to link with
    std::string objectName; // name of Scala object
    std::vector<Function> functions;
    std::vector<TypeDef> typeDefs;
    std::vector<Struct> structs;
    std::vector<Union> unions;
    std::vector<Enum> enums;
    std::vector<LiteralDefine> literalDefines;
    bool generated = false; // generate type defs only once
    std::string packageName;
};

#endif // SCALA_NATIVE_BINDGEN_INTERMEDIATEREPRESENTATION_H
