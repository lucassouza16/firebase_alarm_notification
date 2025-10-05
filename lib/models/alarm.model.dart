class AlarmModel {
  final String nome;
  final int id;

  AlarmModel({required this.nome, required this.id});

  @override
  bool operator ==(other) {
    return (other is AlarmModel) && other.id == id;
  }

  @override
  int get hashCode => id.hashCode;

  factory AlarmModel.fromJson(Map<String, dynamic> json) {
    return AlarmModel(
      id: json['id'],
      nome: json['nome'],
    );
  }
}
