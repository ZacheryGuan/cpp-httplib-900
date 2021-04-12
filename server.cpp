#include <iostream>
#include "httplib.h"
using httplib::Request;
using httplib::Response;
using httplib::ContentReader;
int main() {
  httplib::Server svr;

  svr.Get("/api", [](const httplib::Request &, httplib::Response &res) {
    res.set_chunked_content_provider("text/plain",
                                     [](size_t offset, httplib::DataSink &sink) {
                                       // construct a large chunk, if the chunk is small, the program will not block.
                                       size_t dataLen = 1 * 1024 * 100;
                                       char dataBuffer[dataLen];
                                       for (int i = 0; i < dataLen; i++) {
                                         dataBuffer[i] = '0' + i % 10;
                                       }
                                       // write the large chunk
                                       for (int i = 0; i < 1024 * 1024; i++) {
                                         std::cout << "write " << i << " th" << std::endl;
                                         sink.write(dataBuffer, dataLen);  // the program BLOCKS here
                                       }
                                       sink.done();
                                       std::cout << "send all" << std::endl;
                                       return true;
                                     });
  });

  svr.listen("127.0.0.1", 8080);

  return 0;
}
