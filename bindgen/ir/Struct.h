#ifndef SCALA_NATIVE_BINDGEN_STRUCT_H
#define SCALA_NATIVE_BINDGEN_STRUCT_H

#include "TypeAndName.h"
#include "TypeDef.h"
#include "types/ArrayType.h"
#include <string>
#include <vector>

#define SCALA_NATIVE_MAX_STRUCT_FIELDS 22

class Field : public TypeAndName {
  public:
    Field(std::string name, std::shared_ptr<Type> type);

    std::string generateSetter(int fieldIndex);

    std::string generateGetter(int fieldIndex);
};

class StructOrUnion {
  public:
    StructOrUnion(std::string name, std::vector<Field *> fields);

    ~StructOrUnion();

    virtual std::shared_ptr<TypeDef>
    generateTypeDef(std::shared_ptr<Location> location) = 0;

    virtual std::string generateHelperClass() const = 0;

    std::string getName() const;

    bool operator==(const StructOrUnion &other) const;

  protected:
    std::string name;
    std::vector<Field *> fields;
};

class Struct : public StructOrUnion,
               public Type,
               public std::enable_shared_from_this<Struct> {
  public:
    Struct(std::string name, std::vector<Field *> fields, uint64_t typeSize);

    std::shared_ptr<TypeDef>
    generateTypeDef(std::shared_ptr<Location> location) override;

    std::string generateHelperClass() const override;

    std::string getAliasType() const;

    /**
     * @return true if helper methods will be generated for this struct
     */
    bool hasHelperMethods() const;

    bool usesType(const std::shared_ptr<Type> &type,
                  bool stopOnTypeDefs) const override;

    std::string str() const override;

    using StructOrUnion::operator==;

  private:
    /* type size is needed if number of fields is bigger than 22 */
    uint64_t typeSize;
};

class Union : public StructOrUnion,
              public ArrayType,
              public std::enable_shared_from_this<Union> {
  public:
    Union(std::string name, std::vector<Field *> fields, uint64_t maxSize);

    std::shared_ptr<TypeDef>
    generateTypeDef(std::shared_ptr<Location> location) override;

    std::string generateHelperClass() const override;

    using StructOrUnion::operator==;

  private:
    std::string getTypeAlias() const;
};

#endif // SCALA_NATIVE_BINDGEN_STRUCT_H
