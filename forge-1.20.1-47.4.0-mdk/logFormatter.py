from datetime import datetime

def log_event(event, file_path="日志.txt"):
    now = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    log_line = f"[{now}] {event}\n"
    with open(file_path, "a", encoding="utf-8") as f:
        f.write(log_line)
    print("Logged:", log_line, end="")

if __name__ == "__main__":
    print("请输入事件，回车记录，输入exit退出：")
    while True:
        event = input("> ")
        if event.strip().lower() == "exit":
            break
        log_event(event)